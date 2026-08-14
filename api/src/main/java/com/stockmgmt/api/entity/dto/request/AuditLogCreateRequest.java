package com.stockmgmt.api.entity.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuditLogCreateRequest {
    private UUID storeId;
    private UUID userId;
    private String action;
    private String entityType;
    private String entityId;
    private String oldValue;
    private String newValue;
}
