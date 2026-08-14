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
public class SubscriptionPlanResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BillingInterval billingInterval;
    private int maxProducts;
    private int maxUsers;
    private int maxBranches;
    private boolean whatsappEnabled;
    private boolean advancedReportsEnabled;
    private boolean apiEnabled;
    private boolean active;
    private String features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}