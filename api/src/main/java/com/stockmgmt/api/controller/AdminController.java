package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreatePlanRequest;
import com.stockmgmt.api.entity.dto.response.*;
import com.stockmgmt.api.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/plans")
    public ResponseEntity<SubscriptionPlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(adminService.createPlan(request));
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanResponse>> getPlans() {
        return ResponseEntity.ok(adminService.getAllPlans());
    }

    @PutMapping("/plans/{planId}")
    public ResponseEntity<SubscriptionPlanResponse> updatePlan(@PathVariable UUID planId,
                                                               @Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(adminService.updatePlan(planId, request));
    }

    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID planId) {
        adminService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<AuthResponse>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}