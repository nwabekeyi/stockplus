package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.response.DashboardStatsResponse;
import com.stockmgmt.api.service.DashboardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardQueryController {

    private final DashboardQueryService dashboardQueryService;

    @GetMapping("/stores/{storeId}/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(@PathVariable UUID storeId) {
        return ResponseEntity.ok(dashboardQueryService.getDashboardStats(storeId));
    }
}
