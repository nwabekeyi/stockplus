package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.BillingInterval;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InitiateSubscriptionRequest {
    private UUID planId;
    private BillingInterval billingInterval;
}