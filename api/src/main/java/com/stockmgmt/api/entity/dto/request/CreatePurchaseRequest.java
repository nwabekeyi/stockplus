package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.PurchaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreatePurchaseRequest {
    @NotBlank
    private String reference;

    @NotNull
    private UUID storeId;

    private UUID supplierId;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private BigDecimal totalCost;

    @NotNull
    private BigDecimal amountPaid;

    private PurchaseStatus status;

    private String notes;

    private List<CreatePurchaseItemRequest> items;
}
