package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateExpenseRequest;
import com.stockmgmt.api.entity.dto.response.ExpenseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponse createExpense(UUID storeId, CreateExpenseRequest request);
    List<ExpenseResponse> getExpenses(UUID storeId);
    List<ExpenseResponse> getExpensesByDateRange(UUID storeId, LocalDateTime start, LocalDateTime end);
    BigDecimal getTotalExpenses(UUID storeId, LocalDateTime start, LocalDateTime end);
}
