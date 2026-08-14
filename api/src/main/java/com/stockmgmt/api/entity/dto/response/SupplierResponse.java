package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.SupplierStatus;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SupplierResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private BigDecimal outstandingBalance;
    private SupplierStatus status;
    private LocalDateTime createdAt;
}
