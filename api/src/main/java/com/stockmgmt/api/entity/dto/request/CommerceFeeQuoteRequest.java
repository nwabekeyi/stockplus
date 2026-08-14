package com.stockmgmt.api.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CommerceFeeQuoteRequest {
    @NotNull
    private UUID planId;
    @NotNull
    private BigDecimal orderAmount;
    private BigDecimal paymentProcessingFee = BigDecimal.ZERO;
}
