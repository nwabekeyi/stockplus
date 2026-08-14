package com.stockmgmt.api.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePurchaseItemRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal costPrice;
}
