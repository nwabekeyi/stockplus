package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateCustomerRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @Email
    private String email;

    private String address;

    private BigDecimal creditLimit;

    private CustomerStatus status;

    private UUID storeId;
}
