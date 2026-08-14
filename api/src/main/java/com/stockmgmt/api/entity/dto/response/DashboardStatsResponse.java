package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class DashboardStatsResponse {
    private int totalProducts;
    private int lowStockCount;
    private int totalSalesToday;
    private BigDecimal revenueToday;
    private BigDecimal revenueThisMonth;
    private int totalSalesThisMonth;
    private BigDecimal customerDebt;
    private BigDecimal supplierDebt;
    private BigDecimal expensesToday;
    private int totalCustomers;
    private int totalSuppliers;
}