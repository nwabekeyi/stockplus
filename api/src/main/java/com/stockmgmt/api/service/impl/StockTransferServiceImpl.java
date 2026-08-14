package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.*;
import com.stockmgmt.api.entity.dto.request.CreateStockTransferRequest;
import com.stockmgmt.api.entity.dto.response.StockTransferResponse;
import com.stockmgmt.api.entity.enumeration.TransferStatus;
import com.stockmgmt.api.entity.enumeration.UnitOfMeasure;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.*;
import com.stockmgmt.api.service.ProductService;
import com.stockmgmt.api.service.StockMovementService;
import com.stockmgmt.api.service.StockTransferService;
import com.stockmgmt.api.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final StockMovementService stockMovementService;
    private final ProductService productService;
    private final StoreService storeService;

    @Override
    @Transactional
    public StockTransferResponse createTransfer(UUID fromStoreId, CreateStockTransferRequest request) {
        Store fromStore = storeService.getStore(fromStoreId);
        Store toStore = storeRepository.findById(request.getToStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("To store not found"));

        if (fromStore.getId().equals(toStore.getId())) {
            throw new RuntimeException("Cannot transfer to the same store");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Stock stock = stockRepository.findByProduct_Id(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        if (stock.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock for transfer");
        }

        StockTransfer transfer = StockTransfer.builder()
                .reference(request.getReference())
                .fromStore(fromStore)
                .toStore(toStore)
                .product(product)
                .quantity(request.getQuantity())
                .status(TransferStatus.PENDING)
                .notes(request.getNotes())
                .build();

        stockTransferRepository.save(transfer);
        return mapToResponse(transfer);
    }

    @Override
    @Transactional
    public StockTransferResponse receiveTransfer(UUID transferId) {
        StockTransfer transfer = stockTransferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));

        if (transfer.getStatus() == TransferStatus.RECEIVED) {
            throw new RuntimeException("Transfer already received");
        }

        Stock fromStock = stockRepository.findByProduct_Id(transfer.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        if (fromStock.getQuantity() < transfer.getQuantity()) {
            throw new RuntimeException("Insufficient stock in source store");
        }

        fromStock.setQuantity(fromStock.getQuantity() - transfer.getQuantity());
        stockRepository.save(fromStock);

        Stock toStock = stockRepository.findByProduct_Id(transfer.getProduct().getId())
                .orElseGet(() -> {
                    Stock newStock = Stock.builder()
                            .product(transfer.getProduct())
                            .quantity(0)
                            .lowStockThreshold(10)
                            .unit(UnitOfMeasure.PIECE)
                            .trackInventory(true)
                            .build();
                    return stockRepository.save(newStock);
                });

        toStock.setQuantity(toStock.getQuantity() + transfer.getQuantity());
        stockRepository.save(toStock);

        stockMovementService.createMovement(transfer.getFromStore().getId(),
                new com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest() {{
                    setProductId(transfer.getProduct().getId());
                    setStoreId(transfer.getFromStore().getId());
                    setQuantity(transfer.getQuantity());
                    setMovementType(com.stockmgmt.api.entity.enumeration.MovementType.TRANSFER);
                    setReference(transfer.getReference());
                    setReason("Transfer out");
                }});

        stockMovementService.createMovement(transfer.getToStore().getId(),
                new com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest() {{
                    setProductId(transfer.getProduct().getId());
                    setStoreId(transfer.getToStore().getId());
                    setQuantity(transfer.getQuantity());
                    setMovementType(com.stockmgmt.api.entity.enumeration.MovementType.TRANSFER);
                    setReference(transfer.getReference());
                    setReason("Transfer in");
                }});

        transfer.setStatus(TransferStatus.RECEIVED);
        transfer.setReceivedAt(LocalDateTime.now());
        stockTransferRepository.save(transfer);

        return mapToResponse(transfer);
    }

    @Override
    public List<StockTransferResponse> getTransfers(UUID storeId) {
        return stockTransferRepository.findByFromStore_IdOrToStoreId(storeId, storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StockTransferResponse> getTransfersByStatus(TransferStatus status) {
        return stockTransferRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private StockTransferResponse mapToResponse(StockTransfer transfer) {
        return StockTransferResponse.builder()
                .id(transfer.getId())
                .reference(transfer.getReference())
                .fromStoreId(transfer.getFromStore().getId())
                .toStoreId(transfer.getToStore().getId())
                .productId(transfer.getProduct().getId())
                .productName(transfer.getProduct().getName())
                .quantity(transfer.getQuantity())
                .status(transfer.getStatus())
                .notes(transfer.getNotes())
                .createdAt(transfer.getCreatedAt())
                .receivedAt(transfer.getReceivedAt())
                .build();
    }
}
