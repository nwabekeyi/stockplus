package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateStockTransferRequest;
import com.stockmgmt.api.entity.dto.response.StockTransferResponse;
import com.stockmgmt.api.service.StockTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService stockTransferService;

    @PostMapping("/stores/{storeId}/transfers")
    public ResponseEntity<StockTransferResponse> createTransfer(@PathVariable UUID storeId,
                                                                @Valid @RequestBody CreateStockTransferRequest request) {
        return ResponseEntity.ok(stockTransferService.createTransfer(storeId, request));
    }

    @PostMapping("/transfers/{transferId}/receive")
    public ResponseEntity<StockTransferResponse> receiveTransfer(@PathVariable UUID transferId) {
        return ResponseEntity.ok(stockTransferService.receiveTransfer(transferId));
    }

    @GetMapping("/stores/{storeId}/transfers")
    public ResponseEntity<List<StockTransferResponse>> getTransfers(@PathVariable UUID storeId) {
        return ResponseEntity.ok(stockTransferService.getTransfers(storeId));
    }
}
