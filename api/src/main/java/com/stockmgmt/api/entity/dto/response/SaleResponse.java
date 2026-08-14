package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SaleResponse {
    private UUID id;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private BigDecimal totalCost;
    private BigDecimal profit;
    private LocalDateTime saleDate;
    private String paymentMethod;
    private String notes;
    private List<SaleItemResponse> items;
}