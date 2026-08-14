package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.BillingInterval;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SubscriptionResponse {
    private UUID id;
    private SubscriptionStatus status;
    private String paystackSubscriptionCode;
    private String authorizationUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean autoRenew;
    private SubscriptionPlanResponse plan;
}