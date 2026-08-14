package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest;
import com.stockmgmt.api.entity.dto.response.StockMovementResponse;

import java.util.List;
import java.util.UUID;

public interface StockMovementService {
    StockMovementResponse createMovement(UUID storeId, CreateStockMovementRequest request);
    List<StockMovementResponse> getMovements(UUID storeId);
    List<StockMovementResponse> getMovementsByProduct(UUID productId);
    List<StockMovementResponse> getMovementsByType(UUID storeId, com.stockmgmt.api.entity.enumeration.MovementType type);
}
