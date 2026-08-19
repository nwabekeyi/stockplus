package com.stockmgmt.api.config;

import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.enumeration.BillingInterval;
import com.stockmgmt.api.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(String... args) {
        List<SubscriptionPlan> requiredPlans = List.of(
                plan("Free Offline", "Frontend-only IndexedDB workspace for businesses that are not ready for cloud sync.", "0", "0", 100, 1, 1, false, false, false, false, "0", 0, false, "IndexedDB backup, offline POS, manual export, no backend database sync"),
                plan("Starter", "For small businesses getting started with digital inventory and POS management.", "5000", "50000", 500, 1, 1, false, false, false, false, "0", 14, false, "POS, inventory, barcode scanning, purchasing, suppliers, expenses, basic profit reports, customers, offline PWA, PDF receipts, basic audit trail"),
                plan("Business", "Everything a growing store needs to manage operations and sell through WhatsApp.", "10000", "100000", 5000, 5, 1, true, true, true, false, "1.00", 14, true, "Inventory, POS, purchases, suppliers, expenses, customers, staff, offline PWA, WhatsApp Commerce, WhatsApp Business Inbox, automated/human conversations, reports, audit trail, advanced permissions"),
                plan("Growth", "For larger businesses, more staff, advanced analytics and multi-branch operations.", "20000", "200000", -1, 15, 5, true, true, true, true, "0.50", 14, false, "Unlimited products, WhatsApp Commerce, advanced analytics, multiple branches, API access, priority support and advanced audit trail"),
                plan("Enterprise", "Custom pricing for large organizations requiring integrations, onboarding, SLAs and dedicated support.", "0", null, -1, -1, -1, true, true, true, true, "0", 0, false, "Custom onboarding, dedicated account management, ERP/accounting integrations, high transaction volumes, custom reporting and negotiated WhatsApp Commerce commission")
        );

        requiredPlans.forEach(this::upsertPlan);
    }

    private void upsertPlan(SubscriptionPlan seed) {
        SubscriptionPlan plan = planRepository.findByName(seed.getName()).orElse(seed);
        plan.setDescription(seed.getDescription());
        plan.setPrice(seed.getPrice());
        plan.setAnnualPrice(seed.getAnnualPrice());
        plan.setBillingInterval(seed.getBillingInterval());
        plan.setMaxProducts(seed.getMaxProducts());
        plan.setMaxUsers(seed.getMaxUsers());
        plan.setMaxBranches(seed.getMaxBranches());
        plan.setTrialDays(seed.getTrialDays());
        plan.setHeroPlan(seed.isHeroPlan());
        plan.setWhatsappEnabled(seed.isWhatsappEnabled());
        plan.setWhatsappCommerceEnabled(seed.isWhatsappCommerceEnabled());
        plan.setWhatsappCommerceCommissionPercent(seed.getWhatsappCommerceCommissionPercent());
        plan.setAdvancedReportsEnabled(seed.isAdvancedReportsEnabled());
        plan.setApiEnabled(seed.isApiEnabled());
        plan.setActive(seed.isActive());
        plan.setFeatures(seed.getFeatures());
        planRepository.save(plan);
    }

    private SubscriptionPlan plan(String name, String description, String monthlyPrice, String annualPrice, int products, int users, int branches, boolean whatsapp, boolean commerce, boolean advancedReports, boolean api, String commission, int trialDays, boolean heroPlan, String features) {
        return SubscriptionPlan.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(monthlyPrice))
                .annualPrice(annualPrice == null ? null : new BigDecimal(annualPrice))
                .billingInterval(BillingInterval.MONTHLY)
                .maxProducts(products)
                .maxUsers(users)
                .maxBranches(branches)
                .trialDays(trialDays)
                .heroPlan(heroPlan)
                .whatsappEnabled(whatsapp)
                .whatsappCommerceEnabled(commerce)
                .whatsappCommerceCommissionPercent(new BigDecimal(commission))
                .advancedReportsEnabled(advancedReports)
                .apiEnabled(api)
                .active(true)
                .features(features)
                .build();
    }
}
