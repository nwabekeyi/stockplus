package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CommerceFeeQuoteResponse {
    private UUID planId;
    private String planName;
    private BigDecimal orderAmount;
    private BigDecimal commissionPercent;
    private BigDecimal platformCommission;
    private BigDecimal paymentProcessingFee;
    private BigDecimal merchantSettlement;
    private boolean whatsappCommerceEnabled;
}
