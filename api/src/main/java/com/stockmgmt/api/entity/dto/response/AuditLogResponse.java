package com.stockmgmt.api.entity.dto.response;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private UUID storeId;
    private String action;
    private String entityType;
    private String entityId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private LocalDateTime createdAt;
}
