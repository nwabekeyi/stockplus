package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.MovementType;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockMovementResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private UUID storeId;
    private int quantity;
    private MovementType movementType;
    private int previousQuantity;
    private int newQuantity;
    private String reference;
    private String reason;
    private UUID userId;
    private LocalDateTime createdAt;
}
