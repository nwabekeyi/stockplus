package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.PurchaseStatus;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PurchaseResponse {
    private UUID id;
    private String reference;
    private UUID storeId;
    private UUID supplierId;
    private BigDecimal totalAmount;
    private BigDecimal totalCost;
    private BigDecimal amountPaid;
    private BigDecimal outstanding;
    private PurchaseStatus status;
    private LocalDateTime purchaseDate;
    private String notes;
    private List<PurchaseItemResponse> items;
}
