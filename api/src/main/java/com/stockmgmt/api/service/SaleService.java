package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateSaleRequest;
import com.stockmgmt.api.entity.dto.response.SaleResponse;
import com.stockmgmt.api.entity.dto.response.DashboardStatsResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleService {
    SaleResponse createSale(UUID storeId, CreateSaleRequest request);
    List<SaleResponse> getSales(UUID storeId);
    SaleResponse getSale(UUID storeId, UUID saleId);
    DashboardStatsResponse getDashboardStats(UUID storeId);
    List<SaleResponse> getSalesByDateRange(UUID storeId, LocalDateTime start, LocalDateTime end);
}