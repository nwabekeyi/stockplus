package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.CustomerStatus;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private BigDecimal creditLimit;
    private BigDecimal outstandingBalance;
    private CustomerStatus status;
    private LocalDateTime createdAt;
}
