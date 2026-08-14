package com.stockmgmt.api.entity.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateReturnItemRequest {
    @NotNull
    private UUID productId;
    @Min(1)
    private int quantity;
    @NotNull
    private BigDecimal unitPrice;
    private boolean restock = true;
}
