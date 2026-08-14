package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.BillingInterval;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreatePlanRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BillingInterval billingInterval;
    private int maxProducts;
    private int maxUsers;
    private int maxBranches;
    private int trialDays;
    private BigDecimal annualPrice;
    private boolean heroPlan;
    private boolean whatsappEnabled;
    private boolean whatsappCommerceEnabled;
    private BigDecimal whatsappCommerceCommissionPercent;
    private boolean advancedReportsEnabled;
    private boolean apiEnabled;
    private String features;
    private boolean active;
}