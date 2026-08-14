package com.stockmgmt.api.config;

import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.enumeration.BillingInterval;
import com.stockmgmt.api.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(String... args) {
        if (planRepository.count() == 0) {
            SubscriptionPlan freeTier = SubscriptionPlan.builder()
                    .name("Free Tier")
                    .description("Free plan for new businesses")
                    .price(BigDecimal.ZERO)
                    .billingInterval(BillingInterval.MONTHLY)
                    .maxProducts(50)
                    .maxUsers(2)
                    .maxBranches(1)
                    .whatsappEnabled(false)
                    .advancedReportsEnabled(false)
                    .apiEnabled(false)
                    .active(true)
                    .features("Basic inventory management")
                    .build();

            planRepository.save(freeTier);
        }
    }
}
