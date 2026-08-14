package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateExpenseRequest {
    @NotNull
    private UUID storeId;

    @NotNull
    private ExpenseCategory category;

    @NotNull
    private BigDecimal amount;

    private String description;

    private String receipt;

    private String createdBy;
}
