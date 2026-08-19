package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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