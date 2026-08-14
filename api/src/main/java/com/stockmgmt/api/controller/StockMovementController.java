package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateStockMovementRequest;
import com.stockmgmt.api.entity.dto.response.StockMovementResponse;
import com.stockmgmt.api.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping("/stores/{storeId}/stock-movements")
    public ResponseEntity<StockMovementResponse> createMovement(@PathVariable UUID storeId,
                                                                @Valid @RequestBody CreateStockMovementRequest request) {
        return ResponseEntity.ok(stockMovementService.createMovement(storeId, request));
    }

    @GetMapping("/stores/{storeId}/stock-movements")
    public ResponseEntity<List<StockMovementResponse>> getMovements(@PathVariable UUID storeId) {
        return ResponseEntity.ok(stockMovementService.getMovements(storeId));
    }

    @GetMapping("/stores/{storeId}/stock-movements/product/{productId}")
    public ResponseEntity<List<StockMovementResponse>> getMovementsByProduct(@PathVariable UUID storeId, @PathVariable UUID productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByProduct(productId));
    }
}
