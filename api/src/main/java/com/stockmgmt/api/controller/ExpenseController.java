package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateExpenseRequest;
import com.stockmgmt.api.entity.dto.response.ExpenseResponse;
import com.stockmgmt.api.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/stores/{storeId}/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@PathVariable UUID storeId,
                                                         @Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.ok(expenseService.createExpense(storeId, request));
    }

    @GetMapping("/stores/{storeId}/expenses")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(@PathVariable UUID storeId) {
        return ResponseEntity.ok(expenseService.getExpenses(storeId));
    }

    @GetMapping("/stores/{storeId}/expenses/range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByRange(@PathVariable UUID storeId,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(storeId, start, end));
    }

    @GetMapping("/stores/{storeId}/expenses/total")
    public ResponseEntity<BigDecimal> getTotalExpenses(@PathVariable UUID storeId,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(expenseService.getTotalExpenses(storeId, start, end));
    }
}
