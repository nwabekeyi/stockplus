package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CommerceFeeQuoteRequest;
import com.stockmgmt.api.entity.dto.request.InitiateSubscriptionRequest;
import com.stockmgmt.api.entity.dto.request.VerifySubscriptionRequest;
import com.stockmgmt.api.entity.dto.response.*;
import com.stockmgmt.api.service.AdminService;
import com.stockmgmt.api.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final AdminService adminService;

    @GetMapping("/subscriptions/plans")
    public ResponseEntity<List<SubscriptionPlanResponse>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getAvailablePlans());
    }

    @GetMapping("/subscriptions/current")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(@RequestParam UUID storeId) {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(storeId));
    }

    @PostMapping("/subscriptions/trial")
    public ResponseEntity<SubscriptionResponse> startTrial(@RequestParam UUID storeId) {
        return ResponseEntity.ok(subscriptionService.startTrial(storeId));
    }

    @PostMapping("/subscriptions/commerce-fee-quote")
    public ResponseEntity<CommerceFeeQuoteResponse> quoteCommerceFees(@Valid @RequestBody CommerceFeeQuoteRequest request) {
        return ResponseEntity.ok(subscriptionService.quoteCommerceFees(request));
    }

    @PostMapping("/subscriptions/initiate")
    public ResponseEntity<SubscriptionResponse> initiateSubscription(@RequestParam UUID storeId,
                                                                     @Valid @RequestBody InitiateSubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.initiateSubscription(storeId, request));
    }

    @PostMapping("/subscriptions/verify")
    public ResponseEntity<SubscriptionResponse> verifySubscription(@RequestParam UUID storeId,
                                                                   @Valid @RequestBody VerifySubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.verifySubscription(storeId, request));
    }
}