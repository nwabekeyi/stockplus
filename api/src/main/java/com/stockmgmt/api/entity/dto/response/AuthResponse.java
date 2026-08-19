package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean hasStore;
    private UUID storeId;
    private String storeName;
    private String storeCurrency;
}