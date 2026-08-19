package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.request.CreateStoreRequest;

import java.util.UUID;

public interface StoreService {
    Store createStore(Store store);
    Store getStore(UUID storeId);
    Store getStoreByOwner(UUID ownerId);
    com.stockmgmt.api.entity.dto.response.StoreResponse createStoreForUser(UUID userId, CreateStoreRequest request);
    com.stockmgmt.api.entity.dto.response.StoreResponse updateStore(UUID storeId, CreateStoreRequest request);
    void deleteStore(UUID storeId);
}
