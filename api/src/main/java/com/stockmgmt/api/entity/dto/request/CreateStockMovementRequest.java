package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.MovementType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStockMovementRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private UUID storeId;

    @NotNull
    private int quantity;

    @NotNull
    private MovementType movementType;

    private String reference;

    private String reason;

    private UUID userId;
}
