package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreatePlanRequest;
import com.stockmgmt.api.entity.dto.response.SubscriptionPlanResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    SubscriptionPlanResponse createPlan(CreatePlanRequest request);
    List<SubscriptionPlanResponse> getAllPlans();
    SubscriptionPlanResponse updatePlan(UUID planId, CreatePlanRequest request);
    void deletePlan(UUID planId);
    List<com.stockmgmt.api.entity.dto.response.AuthResponse> getAllUsers();
}