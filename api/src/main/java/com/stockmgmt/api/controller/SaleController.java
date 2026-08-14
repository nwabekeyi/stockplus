package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateSaleRequest;
import com.stockmgmt.api.entity.dto.response.*;
import com.stockmgmt.api.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/stores/{storeId}/sales")
    public ResponseEntity<SaleResponse> createSale(@PathVariable UUID storeId,
                                                   @Valid @RequestBody CreateSaleRequest request) {
        return ResponseEntity.ok(saleService.createSale(storeId, request));
    }

    @GetMapping("/stores/{storeId}/sales")
    public ResponseEntity<List<SaleResponse>> getSales(@PathVariable UUID storeId) {
        return ResponseEntity.ok(saleService.getSales(storeId));
    }

    @GetMapping("/stores/{storeId}/sales/{saleId}")
    public ResponseEntity<SaleResponse> getSale(@PathVariable UUID storeId, @PathVariable UUID saleId) {
        return ResponseEntity.ok(saleService.getSale(storeId, saleId));
    }

    @GetMapping("/stores/{storeId}/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getStats(@PathVariable UUID storeId) {
        return ResponseEntity.ok(saleService.getDashboardStats(storeId));
    }

    @GetMapping("/stores/{storeId}/sales/range")
    public ResponseEntity<List<SaleResponse>> getSalesByRange(@PathVariable UUID storeId,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(saleService.getSalesByDateRange(storeId, start, end));
    }
}