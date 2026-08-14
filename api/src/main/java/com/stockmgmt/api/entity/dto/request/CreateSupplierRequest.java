package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.SupplierStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSupplierRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @Email
    private String email;

    private String address;

    private SupplierStatus status;

    private UUID storeId;
}
