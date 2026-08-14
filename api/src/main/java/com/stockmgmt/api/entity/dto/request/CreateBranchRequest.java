package com.stockmgmt.api.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateBranchRequest {
    @NotBlank
    private String name;

    private String address;

    private String phone;

    private String manager;

    @NotNull
    private UUID storeId;
}
