package com.stockmgmt.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.stockmgmt.api.entity.enumeration.BillingInterval;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingInterval billingInterval = BillingInterval.MONTHLY;

    @Column(nullable = false)
    private int maxProducts;

    @Column(nullable = false)
    private int maxUsers;

    @Column(nullable = false)
    private int maxBranches;

    @Column(nullable = false)
    @Builder.Default
    private int trialDays = 14;

    @Column(precision = 10, scale = 2)
    private BigDecimal annualPrice;

    @Column(nullable = false)
    @Builder.Default
    private boolean heroPlan = false;

    @Column(nullable = false)
    private boolean whatsappEnabled = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean whatsappCommerceEnabled = false;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal whatsappCommerceCommissionPercent = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean advancedReportsEnabled = false;

    @Column(nullable = false)
    private boolean apiEnabled = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column
    private String features;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}