package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.response.DashboardStatsResponse;
import com.stockmgmt.api.entity.dto.response.FinancialSummaryResponse;
import com.stockmgmt.api.service.SaleService;
import com.stockmgmt.api.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final SaleService saleService;
    private final ExpenseService expenseService;

    @GetMapping("/stores/{storeId}/reports/financial")
    public ResponseEntity<FinancialSummaryResponse> getFinancialSummary(@PathVariable UUID storeId,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        var sales = saleService.getSalesByDateRange(storeId, startDateTime, endDateTime);
        BigDecimal totalSales = sales.stream()
                .map(s -> s.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = sales.stream()
                .map(s -> s.getTotalCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grossProfit = totalSales.subtract(totalCost);

        BigDecimal totalExpenses = expenseService.getTotalExpenses(storeId, startDateTime, endDateTime);

        BigDecimal netProfit = grossProfit.subtract(totalExpenses);

        return ResponseEntity.ok(FinancialSummaryResponse.builder()
                .totalSales(totalSales)
                .totalCost(totalCost)
                .grossProfit(grossProfit)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .date(end)
                .build());
    }

    @GetMapping("/stores/{storeId}/reports/sales-summary")
    public ResponseEntity<java.util.Map<String, Object>> getSalesSummary(@PathVariable UUID storeId,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        var sales = saleService.getSalesByDateRange(storeId, startDateTime, endDateTime);
        BigDecimal totalRevenue = sales.stream()
                .map(s -> s.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalTransactions = sales.size();

        return ResponseEntity.ok(java.util.Map.of(
                "totalRevenue", totalRevenue,
                "totalTransactions", totalTransactions,
                "startDate", start,
                "endDate", end
        ));
    }
}
