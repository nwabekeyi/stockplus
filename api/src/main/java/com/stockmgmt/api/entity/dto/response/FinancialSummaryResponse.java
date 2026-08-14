package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FinancialSummaryResponse {
    private BigDecimal totalSales;
    private BigDecimal totalCost;
    private BigDecimal grossProfit;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private BigDecimal customerDebt;
    private BigDecimal supplierDebt;
    private LocalDate date;
}
