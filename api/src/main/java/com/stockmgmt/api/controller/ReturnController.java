package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.Product;
import com.stockmgmt.api.entity.Return;
import com.stockmgmt.api.entity.ReturnItem;
import com.stockmgmt.api.entity.Sale;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.request.CreateReturnRequest;
import com.stockmgmt.api.entity.dto.response.ReturnItemResponse;
import com.stockmgmt.api.entity.dto.response.ReturnResponse;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.ProductRepository;
import com.stockmgmt.api.repository.ReturnRepository;
import com.stockmgmt.api.repository.SaleRepository;
import com.stockmgmt.api.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReturnController {
    private final ReturnRepository returnRepository;
    private final StoreRepository storeRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @PostMapping("/stores/{storeId}/returns")
    @Transactional
    public ResponseEntity<ReturnResponse> createReturn(@PathVariable UUID storeId, @Valid @RequestBody CreateReturnRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        Sale sale = request.getSaleId() == null ? null : saleRepository.findById(request.getSaleId()).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        Return returnRecord = Return.builder()
                .store(store)
                .sale(sale)
                .reference("RET-" + System.currentTimeMillis())
                .reason(request.getReason())
                .refundMethod(request.getRefundMethod())
                .approvedBy(request.getApprovedBy())
                .refundAmount(request.getRefundAmount() == null ? BigDecimal.ZERO : request.getRefundAmount())
                .build();
        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            returnRecord.getItems().add(ReturnItem.builder()
                    .returnRecord(returnRecord)
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .restock(item.isRestock())
                    .build());
        });
        returnRepository.save(returnRecord);
        return ResponseEntity.ok(map(returnRecord));
    }

    @GetMapping("/stores/{storeId}/returns")
    public ResponseEntity<List<ReturnResponse>> getReturns(@PathVariable UUID storeId) {
        return ResponseEntity.ok(returnRepository.findByStore_IdOrderByCreatedAtDesc(storeId).stream().map(this::map).toList());
    }

    private ReturnResponse map(Return returnRecord) {
        return ReturnResponse.builder()
                .id(returnRecord.getId())
                .storeId(returnRecord.getStore().getId())
                .saleId(returnRecord.getSale() == null ? null : returnRecord.getSale().getId())
                .reference(returnRecord.getReference())
                .reason(returnRecord.getReason())
                .refundAmount(returnRecord.getRefundAmount())
                .status(returnRecord.getStatus())
                .refundMethod(returnRecord.getRefundMethod())
                .approvedBy(returnRecord.getApprovedBy())
                .createdAt(returnRecord.getCreatedAt())
                .items(returnRecord.getItems().stream().map(item -> ReturnItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .restock(item.isRestock())
                        .build()).toList())
                .build();
    }
}
