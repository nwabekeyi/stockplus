package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.*;
import com.stockmgmt.api.entity.dto.request.CreateSaleRequest;
import com.stockmgmt.api.entity.dto.request.SaleItemRequest;
import com.stockmgmt.api.entity.dto.response.*;
import com.stockmgmt.api.entity.enumeration.PaymentStatus;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.*;
import com.stockmgmt.api.service.ProductService;
import com.stockmgmt.api.service.SaleService;
import com.stockmgmt.api.service.StoreService;
import com.stockmgmt.api.service.SubscriptionService;
import com.stockmgmt.api.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final StoreService storeService;
    private final AuditLogService auditLogService;
    private final SupplierRepository supplierRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public SaleResponse createSale(UUID storeId, CreateSaleRequest request) {
        Store store = storeService.getStore(storeId);

        Subscription subscription = subscriptionRepository.findByStore_Id(storeId)
                .orElseThrow(() -> new RuntimeException("No active subscription. Please subscribe first."));
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Subscription is not active");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        Sale sale = Sale.builder()
                .store(store)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .saleDate(LocalDateTime.now())
                .paymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.SUCCESS)
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .build();

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElse(null);
            sale.setCustomer(customer);
        }

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            Stock stock = stockRepository.findByProduct_Id(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

            if (stock.isTrackInventory() && stock.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            BigDecimal subtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            totalCost = totalCost.add(product.getCostPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .costPrice(product.getCostPrice())
                    .subtotal(subtotal)
                    .build();
            sale.getItems().add(saleItem);

            if (stock.isTrackInventory()) {
                stock.setQuantity(stock.getQuantity() - itemReq.getQuantity());
                stockRepository.save(stock);
            }
        }

        sale.setTotalAmount(totalAmount.subtract(sale.getDiscount()));
        sale.setTotalCost(totalCost);
        sale.setProfit(totalAmount.subtract(totalCost).subtract(sale.getDiscount()));

        saleRepository.save(sale);

        if (customer != null && sale.getPaymentStatus() == PaymentStatus.PENDING) {
            BigDecimal amountOwed = sale.getTotalAmount().subtract(sale.getDiscount());
            customer.setOutstandingBalance(customer.getOutstandingBalance().add(amountOwed));
            customerRepository.save(customer);
        }

        return mapToResponse(sale);
    }

    @Override
    public List<SaleResponse> getSales(UUID storeId) {
        return saleRepository.findByStore_IdOrderBySaleDateDesc(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SaleResponse getSale(UUID storeId, UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        if (!sale.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Sale does not belong to this store");
        }
        return mapToResponse(sale);
    }

    @Override
    public DashboardStatsResponse getDashboardStats(UUID storeId) {
        List<Sale> todaySales = saleRepository.findByStore_IdAndSaleDateBetween(
                storeId, LocalDate.now().atStartOfDay(), LocalDateTime.now());

        List<Sale> monthSales = saleRepository.findByStore_IdAndSaleDateBetween(
                storeId, LocalDate.now().withDayOfMonth(1).atStartOfDay(), LocalDateTime.now());

        BigDecimal todayRevenue = todaySales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthRevenue = monthSales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int lowStockCount = (int) stockRepository.findByQuantityLessThanEqual(10).stream()
                .filter(s -> s.getProduct().getStore().getId().equals(storeId))
                .count();

        int totalProducts = (int) productRepository.findByStore_Id(storeId).stream()
                .filter(p -> p.isActive())
                .count();

        BigDecimal customerDebt = customerRepository.findByStore_Id(storeId).stream()
                .map(Customer::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal supplierDebt = BigDecimal.ZERO;

        int totalCustomers = customerRepository.findByStore_Id(storeId).size();
        int totalSuppliers = supplierRepository.findByStore_Id(storeId).size();

        BigDecimal expensesToday = expenseRepository.sumAmountByStoreIdAndExpenseDateBetween(
                storeId, LocalDate.now().atStartOfDay(), LocalDateTime.now());
        if (expensesToday == null) expensesToday = BigDecimal.ZERO;

        return DashboardStatsResponse.builder()
                .totalProducts(totalProducts)
                .lowStockCount(lowStockCount)
                .totalSalesToday(todaySales.size())
                .revenueToday(todayRevenue)
                .revenueThisMonth(monthRevenue)
                .totalSalesThisMonth(monthSales.size())
                .customerDebt(customerDebt)
                .supplierDebt(supplierDebt)
                .expensesToday(expensesToday)
                .totalCustomers(totalCustomers)
                .totalSuppliers(totalSuppliers)
                .build();
    }

    @Override
    public List<SaleResponse> getSalesByDateRange(UUID storeId, LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByStore_IdAndSaleDateBetween(storeId, start, end).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SaleResponse mapToResponse(Sale sale) {
        List<SaleItemResponse> items = sale.getItems().stream()
                .map(item -> SaleItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .costPrice(item.getCostPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .customerName(sale.getCustomerName())
                .customerPhone(sale.getCustomerPhone())
                .totalAmount(sale.getTotalAmount())
                .totalCost(sale.getTotalCost())
                .profit(sale.getProfit())
                .saleDate(sale.getSaleDate())
                .paymentMethod(sale.getPaymentMethod())
                .notes(sale.getNotes())
                .items(items)
                .build();
    }
}