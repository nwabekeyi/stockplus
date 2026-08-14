package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.*;
import com.stockmgmt.api.entity.dto.request.CreatePurchaseItemRequest;
import com.stockmgmt.api.entity.dto.request.CreatePurchaseRequest;
import com.stockmgmt.api.entity.dto.response.PurchaseItemResponse;
import com.stockmgmt.api.entity.dto.response.PurchaseResponse;
import com.stockmgmt.api.entity.enumeration.PurchaseStatus;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.*;
import com.stockmgmt.api.service.ProductService;
import com.stockmgmt.api.service.PurchaseService;
import com.stockmgmt.api.service.StockMovementService;
import com.stockmgmt.api.service.StoreService;
import com.stockmgmt.api.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StoreRepository storeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final StoreService storeService;
    private final StockMovementService stockMovementService;
    private final ProductService productService;

    @Override
    @Transactional
    public PurchaseResponse createPurchase(UUID storeId, CreatePurchaseRequest request) {
        Store store = storeService.getStore(storeId);

        Subscription subscription = subscriptionRepository.findByStore_Id(storeId)
                .orElseThrow(() -> new RuntimeException("No active subscription"));
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Subscription is not active");
        }

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        }

        Purchase purchase = Purchase.builder()
                .reference(request.getReference())
                .store(store)
                .supplier(supplier)
                .totalAmount(request.getTotalAmount())
                .totalCost(request.getTotalCost())
                .amountPaid(request.getAmountPaid() != null ? request.getAmountPaid() : BigDecimal.ZERO)
                .outstanding(request.getTotalAmount().subtract(request.getAmountPaid() != null ? request.getAmountPaid() : BigDecimal.ZERO))
                .status(request.getStatus() != null ? request.getStatus() : PurchaseStatus.PENDING)
                .purchaseDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        purchaseRepository.save(purchase);

        for (CreatePurchaseItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            PurchaseItem purchaseItem = PurchaseItem.builder()
                    .purchase(purchase)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .costPrice(itemReq.getCostPrice())
                    .subtotal(itemReq.getCostPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())))
                    .build();
            purchaseItemRepository.save(purchaseItem);
            purchase.getItems().add(purchaseItem);

            if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
                Stock stock = stockRepository.findByProduct_Id(product.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
                stock.setQuantity(stock.getQuantity() + itemReq.getQuantity());
                stockRepository.save(stock);

                stockMovementService.createMovement(storeId, new com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest() {{
                    setProductId(product.getId());
                    setStoreId(storeId);
                    setQuantity(itemReq.getQuantity());
                    setMovementType(com.stockmgmt.api.entity.enumeration.MovementType.PURCHASE);
                    setReference(purchase.getReference());
                    setReason("Purchase received");
                }});
            }
        }

        purchaseRepository.save(purchase);
        return mapToResponse(purchase);
    }

    @Override
    public List<PurchaseResponse> getPurchases(UUID storeId) {
        return purchaseRepository.findByStore_Id(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PurchaseResponse getPurchase(UUID storeId, UUID purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        if (!purchase.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Purchase does not belong to this store");
        }
        return mapToResponse(purchase);
    }

    @Override
    @Transactional
    public PurchaseResponse receivePurchase(UUID storeId, UUID purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        if (!purchase.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Purchase does not belong to this store");
        }
        if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
            throw new RuntimeException("Purchase already received");
        }

        for (PurchaseItem item : purchase.getItems()) {
            Stock stock = stockRepository.findByProduct_Id(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockRepository.save(stock);

            stockMovementService.createMovement(storeId, new com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest() {{
                setProductId(item.getProduct().getId());
                setStoreId(storeId);
                setQuantity(item.getQuantity());
                setMovementType(com.stockmgmt.api.entity.enumeration.MovementType.PURCHASE);
                setReference(purchase.getReference());
                setReason("Purchase received");
            }});
        }

        purchase.setStatus(PurchaseStatus.RECEIVED);
        purchaseRepository.save(purchase);
        return mapToResponse(purchase);
    }

    @Override
    @Transactional
    public void deletePurchase(UUID storeId, UUID purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        if (!purchase.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Purchase does not belong to this store");
        }
        if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
            throw new RuntimeException("Cannot delete received purchase");
        }
        purchaseRepository.delete(purchase);
    }

    private PurchaseResponse mapToResponse(Purchase purchase) {
        List<PurchaseItemResponse> items = purchase.getItems().stream()
                .map(item -> PurchaseItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .costPrice(item.getCostPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PurchaseResponse.builder()
                .id(purchase.getId())
                .reference(purchase.getReference())
                .storeId(purchase.getStore().getId())
                .supplierId(purchase.getSupplier() != null ? purchase.getSupplier().getId() : null)
                .totalAmount(purchase.getTotalAmount())
                .totalCost(purchase.getTotalCost())
                .amountPaid(purchase.getAmountPaid())
                .outstanding(purchase.getOutstanding())
                .status(purchase.getStatus())
                .purchaseDate(purchase.getPurchaseDate())
                .notes(purchase.getNotes())
                .items(items)
                .build();
    }
}
