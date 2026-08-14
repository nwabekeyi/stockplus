package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReturnResponse {
    private UUID id;
    private UUID storeId;
    private UUID saleId;
    private String reference;
    private String reason;
    private BigDecimal refundAmount;
    private ReturnStatus status;
    private String refundMethod;
    private String approvedBy;
    private LocalDateTime createdAt;
    private List<ReturnItemResponse> items;
}
