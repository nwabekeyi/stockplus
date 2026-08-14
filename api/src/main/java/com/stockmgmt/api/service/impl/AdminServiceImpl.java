package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.dto.request.CreatePlanRequest;
import com.stockmgmt.api.entity.dto.response.SubscriptionPlanResponse;
import com.stockmgmt.api.entity.enumeration.BillingInterval;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.SubscriptionPlanRepository;
import com.stockmgmt.api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SubscriptionPlanRepository planRepository;
    private final com.stockmgmt.api.repository.UserRepository userRepository;

    @Override
    @Transactional
    public SubscriptionPlanResponse createPlan(CreatePlanRequest request) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .billingInterval(request.getBillingInterval() != null ? request.getBillingInterval() : BillingInterval.MONTHLY)
                .maxProducts(request.getMaxProducts())
                .maxUsers(request.getMaxUsers())
                .maxBranches(request.getMaxBranches())
                .trialDays(request.getTrialDays() == 0 ? 14 : request.getTrialDays())
                .annualPrice(request.getAnnualPrice())
                .heroPlan(request.isHeroPlan())
                .whatsappEnabled(request.isWhatsappEnabled())
                .whatsappCommerceEnabled(request.isWhatsappCommerceEnabled())
                .whatsappCommerceCommissionPercent(request.getWhatsappCommerceCommissionPercent() == null ? BigDecimal.ZERO : request.getWhatsappCommerceCommissionPercent())
                .advancedReportsEnabled(request.isAdvancedReportsEnabled())
                .apiEnabled(request.isApiEnabled())
                .active(request.isActive())
                .features(request.getFeatures())
                .build();

        planRepository.save(plan);
        return mapToResponse(plan);
    }

    @Override
    public List<SubscriptionPlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse updatePlan(UUID planId, CreatePlanRequest request) {
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setBillingInterval(request.getBillingInterval() != null ? request.getBillingInterval() : BillingInterval.MONTHLY);
        plan.setMaxProducts(request.getMaxProducts());
        plan.setMaxUsers(request.getMaxUsers());
        plan.setMaxBranches(request.getMaxBranches());
        plan.setTrialDays(request.getTrialDays() == 0 ? 14 : request.getTrialDays());
        plan.setAnnualPrice(request.getAnnualPrice());
        plan.setHeroPlan(request.isHeroPlan());
        plan.setWhatsappEnabled(request.isWhatsappEnabled());
        plan.setWhatsappCommerceEnabled(request.isWhatsappCommerceEnabled());
        plan.setWhatsappCommerceCommissionPercent(request.getWhatsappCommerceCommissionPercent() == null ? BigDecimal.ZERO : request.getWhatsappCommerceCommissionPercent());
        plan.setAdvancedReportsEnabled(request.isAdvancedReportsEnabled());
        plan.setApiEnabled(request.isApiEnabled());
        plan.setActive(request.isActive());
        plan.setFeatures(request.getFeatures());

        planRepository.save(plan);
        return mapToResponse(plan);
    }

    @Override
    @Transactional
    public void deletePlan(UUID planId) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Plan not found");
        }
        planRepository.deleteById(planId);
    }

    @Override
    public List<com.stockmgmt.api.entity.dto.response.AuthResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> com.stockmgmt.api.entity.dto.response.AuthResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole())
                        .hasStore(user.getStore() != null)
                        .build())
                .toList();
    }

    private SubscriptionPlanResponse mapToResponse(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingInterval(plan.getBillingInterval())
                .maxProducts(plan.getMaxProducts())
                .maxUsers(plan.getMaxUsers())
                .maxBranches(plan.getMaxBranches())
                .trialDays(plan.getTrialDays())
                .annualPrice(plan.getAnnualPrice())
                .heroPlan(plan.isHeroPlan())
                .whatsappEnabled(plan.isWhatsappEnabled())
                .whatsappCommerceEnabled(plan.isWhatsappCommerceEnabled())
                .whatsappCommerceCommissionPercent(plan.getWhatsappCommerceCommissionPercent())
                .advancedReportsEnabled(plan.isAdvancedReportsEnabled())
                .apiEnabled(plan.isApiEnabled())
                .active(plan.isActive())
                .features(plan.getFeatures())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}