package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.ExpenseCategory;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExpenseResponse {
    private UUID id;
    private UUID storeId;
    private ExpenseCategory category;
    private BigDecimal amount;
    private String description;
    private String receipt;
    private LocalDateTime expenseDate;
    private String createdBy;
}
