package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class WholesalePriceRuleResponse {
    private UUID id;
    private int minQuantity;
    private Integer maxQuantity;
    private BigDecimal price;
}
