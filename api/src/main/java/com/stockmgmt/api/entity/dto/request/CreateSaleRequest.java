package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateSaleRequest {
    private String customerName;
    private String customerPhone;
    private String paymentMethod;
    private String notes;
    private PaymentStatus paymentStatus;
    private BigDecimal discount;
    private UUID customerId;
    private List<SaleItemRequest> items;
}