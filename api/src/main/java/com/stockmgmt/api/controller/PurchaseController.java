package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreatePurchaseRequest;
import com.stockmgmt.api.entity.dto.response.PurchaseResponse;
import com.stockmgmt.api.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/stores/{storeId}/purchases")
    public ResponseEntity<PurchaseResponse> createPurchase(@PathVariable UUID storeId,
                                                           @Valid @RequestBody CreatePurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.createPurchase(storeId, request));
    }

    @GetMapping("/stores/{storeId}/purchases")
    public ResponseEntity<List<PurchaseResponse>> getPurchases(@PathVariable UUID storeId) {
        return ResponseEntity.ok(purchaseService.getPurchases(storeId));
    }

    @GetMapping("/stores/{storeId}/purchases/{purchaseId}")
    public ResponseEntity<PurchaseResponse> getPurchase(@PathVariable UUID storeId, @PathVariable UUID purchaseId) {
        return ResponseEntity.ok(purchaseService.getPurchase(storeId, purchaseId));
    }

    @PostMapping("/stores/{storeId}/purchases/{purchaseId}/receive")
    public ResponseEntity<PurchaseResponse> receivePurchase(@PathVariable UUID storeId, @PathVariable UUID purchaseId) {
        return ResponseEntity.ok(purchaseService.receivePurchase(storeId, purchaseId));
    }

    @DeleteMapping("/stores/{storeId}/purchases/{purchaseId}")
    public ResponseEntity<Void> deletePurchase(@PathVariable UUID storeId, @PathVariable UUID purchaseId) {
        purchaseService.deletePurchase(storeId, purchaseId);
        return ResponseEntity.ok().build();
    }
}
