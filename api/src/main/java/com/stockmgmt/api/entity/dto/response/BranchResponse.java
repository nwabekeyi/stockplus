package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BranchResponse {
    private UUID id;
    private String name;
    private String address;
    private String phone;
    private String manager;
    private boolean active;
    private LocalDateTime createdAt;
    private UUID storeId;
}
