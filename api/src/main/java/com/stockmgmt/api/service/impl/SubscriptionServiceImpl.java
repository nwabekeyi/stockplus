package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.config.AppProperties;
import com.stockmgmt.api.entity.*;
import com.stockmgmt.api.entity.dto.request.CreatePlanRequest;
import com.stockmgmt.api.entity.dto.request.InitiateSubscriptionRequest;
import com.stockmgmt.api.entity.dto.request.VerifySubscriptionRequest;
import com.stockmgmt.api.entity.dto.response.PaystackInitResponse;
import com.stockmgmt.api.entity.dto.response.SubscriptionPlanResponse;
import com.stockmgmt.api.entity.dto.response.SubscriptionResponse;
import com.stockmgmt.api.entity.enumeration.*;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.*;
import com.stockmgmt.api.service.SubscriptionService;
import com.stockmgmt.api.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final StoreRepository storeRepository;
    private final StoreService storeService;
    private final AppProperties appProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<SubscriptionPlanResponse> getAvailablePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SubscriptionResponse getCurrentSubscription(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        Subscription subscription = subscriptionRepository.findByStore_Id(storeId)
                .orElse(null);
        if (subscription == null) {
            return null;
        }
        return mapToResponse(subscription);
    }

    @Override
    @Transactional
    public SubscriptionResponse initiateSubscription(UUID storeId, InitiateSubscriptionRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        Subscription existing = subscriptionRepository.findByStore_Id(storeId).orElse(null);
        if (existing != null && existing.getStatus() == SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Active subscription exists. Cancel before changing.");
        }

        Subscription subscription = Subscription.builder()
                .store(store)
                .plan(plan)
                .status(SubscriptionStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .autoRenew(true)
                .build();

        subscriptionRepository.save(subscription);

        PaystackInitResponse paystackResponse = initializePaystackPlan(subscription, plan, request.getBillingInterval());

        subscription.setPaystackSubscriptionCode(paystackResponse.getReference());
        subscriptionRepository.save(subscription);

        SubscriptionResponse response = mapToResponse(subscription);
        response.setAuthorizationUrl(paystackResponse.getAuthorizationUrl());
        return response;
    }

    @Override
    @Transactional
    public SubscriptionResponse verifySubscription(UUID storeId, VerifySubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findByStore_Id(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (!verifyPaystackPayment(request.getReference())) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscription.setPaymentStatus(PaymentStatus.FAILED);
            subscriptionRepository.save(subscription);
            throw new RuntimeException("Payment verification failed");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPaymentStatus(PaymentStatus.SUCCESS);
        subscription.setPaystackAuthorizationCode(request.getAuthorizationCode());
        subscriptionRepository.save(subscription);

        return mapToResponse(subscription);
    }

    private PaystackInitResponse initializePaystackPlan(Subscription subscription, SubscriptionPlan plan, BillingInterval interval) {
        String url = appProperties.getPaystack().getBaseUrl() + "/transaction/initialize";

        Map<String, Object> body = new HashMap<>();
        body.put("email", subscription.getStore().getOwner().getEmail());
        body.put("amount", plan.getPrice().multiply(new BigDecimal("100")));
        body.put("reference", "SUB-" + subscription.getId().toString().replace("-", "").substring(0, 16).toUpperCase());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("subscription_id", subscription.getId().toString());
        metadata.put("plan_id", plan.getId().toString());
        body.put("metadata", metadata);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(appProperties.getPaystack().getSecretKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

        return PaystackInitResponse.builder()
                .authorizationUrl((String) data.get("authorization_url"))
                .accessCode((String) data.get("access_code"))
                .reference((String) data.get("reference"))
                .build();
    }

    private boolean verifyPaystackPayment(String reference) {
        String url = appProperties.getPaystack().getBaseUrl() + "/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(appProperties.getPaystack().getSecretKey());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        return "success".equalsIgnoreCase((String) data.get("status"));
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
                .whatsappEnabled(plan.isWhatsappEnabled())
                .advancedReportsEnabled(plan.isAdvancedReportsEnabled())
                .apiEnabled(plan.isApiEnabled())
                .active(plan.isActive())
                .features(plan.getFeatures())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .status(subscription.getStatus())
                .paystackSubscriptionCode(subscription.getPaystackSubscriptionCode())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.isAutoRenew())
                .plan(mapToResponse(subscription.getPlan()))
                .build();
    }
}