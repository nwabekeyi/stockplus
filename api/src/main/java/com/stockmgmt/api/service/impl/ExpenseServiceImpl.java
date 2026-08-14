package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.Expense;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.request.CreateExpenseRequest;
import com.stockmgmt.api.entity.dto.response.ExpenseResponse;
import com.stockmgmt.api.entity.enumeration.ExpenseCategory;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.ExpenseRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.service.AuditLogService;
import com.stockmgmt.api.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final StoreRepository storeRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ExpenseResponse createExpense(UUID storeId, CreateExpenseRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Expense expense = Expense.builder()
                .store(store)
                .category(request.getCategory())
                .amount(request.getAmount())
                .description(request.getDescription())
                .receipt(request.getReceipt())
                .createdBy(request.getCreatedBy())
                .build();

        expenseRepository.save(expense);
        return mapToResponse(expense);
    }

    @Override
    public List<ExpenseResponse> getExpenses(UUID storeId) {
        return expenseRepository.findByStore_Id(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ExpenseResponse> getExpensesByDateRange(UUID storeId, LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByStore_IdAndExpenseDateBetween(storeId, start, end).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BigDecimal getTotalExpenses(UUID storeId, LocalDateTime start, LocalDateTime end) {
        BigDecimal total = expenseRepository.sumAmountByStoreIdAndExpenseDateBetween(storeId, start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .storeId(expense.getStore().getId())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .receipt(expense.getReceipt())
                .expenseDate(expense.getExpenseDate())
                .createdBy(expense.getCreatedBy())
                .build();
    }
}
