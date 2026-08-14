package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.*;
import com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest;
import com.stockmgmt.api.entity.dto.response.StockMovementResponse;
import com.stockmgmt.api.entity.enumeration.MovementType;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.ProductRepository;
import com.stockmgmt.api.repository.StockMovementRepository;
import com.stockmgmt.api.repository.StockRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public StockMovementResponse createMovement(UUID storeId, CreateStockMovementRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Stock stock = stockRepository.findByProduct_Id(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        int previousQuantity = stock.getQuantity();
        int newQuantity;

        if (request.getMovementType() == MovementType.SALE
                || request.getMovementType() == MovementType.DAMAGED
                || request.getMovementType() == MovementType.ADJUSTMENT) {
            newQuantity = previousQuantity - request.getQuantity();
        } else {
            newQuantity = previousQuantity + request.getQuantity();
        }

        newQuantity = Math.max(newQuantity, 0);

        StockMovement stockMovement = StockMovement.builder()
                .product(product)
                .store(store)
                .quantity(request.getQuantity())
                .movementType(request.getMovementType())
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .reference(request.getReference())
                .reason(request.getReason())
                .userId(request.getUserId())
                .build();

        stockMovementRepository.save(stockMovement);
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);

        return mapToResponse(stockMovement);
    }

    @Override
    public List<StockMovementResponse> getMovements(UUID storeId) {
        return stockMovementRepository.findByStore_IdOrderByCreatedAtDesc(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> getMovementsByProduct(UUID productId) {
        return stockMovementRepository.findByProduct_Id(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> getMovementsByType(UUID storeId, MovementType type) {
        return stockMovementRepository.findByStore_IdAndMovementType(storeId, type).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private StockMovementResponse mapToResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .storeId(movement.getStore().getId())
                .quantity(movement.getQuantity())
                .movementType(movement.getMovementType())
                .previousQuantity(movement.getPreviousQuantity())
                .newQuantity(movement.getNewQuantity())
                .reference(movement.getReference())
                .reason(movement.getReason())
                .userId(movement.getUserId())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
