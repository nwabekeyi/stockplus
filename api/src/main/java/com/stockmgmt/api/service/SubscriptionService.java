package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreatePlanRequest;
import com.stockmgmt.api.entity.dto.request.InitiateSubscriptionRequest;
import com.stockmgmt.api.entity.dto.request.VerifySubscriptionRequest;
import com.stockmgmt.api.entity.dto.response.SubscriptionPlanResponse;
import com.stockmgmt.api.entity.dto.response.SubscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(UUID storeId);
    SubscriptionResponse initiateSubscription(UUID storeId, InitiateSubscriptionRequest request);
    SubscriptionResponse verifySubscription(UUID storeId, VerifySubscriptionRequest request);
    List<SubscriptionPlanResponse> getAvailablePlans();
}