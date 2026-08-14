package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreatePurchaseRequest;
import com.stockmgmt.api.entity.dto.response.PurchaseResponse;

import java.util.List;
import java.util.UUID;

public interface PurchaseService {
    PurchaseResponse createPurchase(UUID storeId, CreatePurchaseRequest request);
    List<PurchaseResponse> getPurchases(UUID storeId);
    PurchaseResponse getPurchase(UUID storeId, UUID purchaseId);
    PurchaseResponse receivePurchase(UUID storeId, UUID purchaseId);
    void deletePurchase(UUID storeId, UUID purchaseId);
}
