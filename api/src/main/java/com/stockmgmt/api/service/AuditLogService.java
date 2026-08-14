package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.AuditLogCreateRequest;
import com.stockmgmt.api.entity.dto.response.AuditLogResponse;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {
    AuditLogResponse createLog(UUID storeId, String action, String entityType, String entityId, String oldValue, String newValue, UUID userId);
    List<AuditLogResponse> getLogs(UUID storeId);
    List<AuditLogResponse> getLogsByUser(UUID userId);
    List<AuditLogResponse> getLogsByEntity(String entityType, String entityId);
}
