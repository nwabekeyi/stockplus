package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.response.DashboardStatsResponse;

import java.util.UUID;

public interface DashboardQueryService {
    DashboardStatsResponse getDashboardStats(UUID storeId);
}
