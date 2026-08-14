package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.response.AuditLogResponse;
import com.stockmgmt.api.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/stores/{storeId}/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getLogs(@PathVariable UUID storeId) {
        return ResponseEntity.ok(auditLogService.getLogs(storeId));
    }

    @GetMapping("/stores/{storeId}/audit-logs/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByUser(@PathVariable UUID storeId, @PathVariable UUID userId) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(userId));
    }

    @GetMapping("/stores/{storeId}/audit-logs/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByEntity(@PathVariable UUID storeId,
                                                                  @PathVariable String entityType,
                                                                  @PathVariable String entityId) {
        return ResponseEntity.ok(auditLogService.getLogsByEntity(entityType, entityId));
    }
}
