package com.stockmgmt.api.entity.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class SaleItemRequest {
    private UUID productId;
    private int quantity;
    private BigDecimal unitPrice;
}