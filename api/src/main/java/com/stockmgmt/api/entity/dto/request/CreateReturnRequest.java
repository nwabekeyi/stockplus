package com.stockmgmt.api.entity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateReturnRequest {
    private UUID saleId;
    @NotBlank
    private String reason;
    private String refundMethod;
    private String approvedBy;
    private BigDecimal refundAmount;
    @NotEmpty
    @Valid
    private List<CreateReturnItemRequest> items;
}
