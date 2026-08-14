package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.AuditLog;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.response.AuditLogResponse;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.AuditLogRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.repository.UserRepository;
import com.stockmgmt.api.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuditLogResponse createLog(UUID storeId, String action, String entityType, String entityId, String oldValue, String newValue, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        AuditLog log = AuditLog.builder()
                .user(userRepository.findById(userId).orElse(null))
                .store(store)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        auditLogRepository.save(log);
        return mapToResponse(log);
    }

    @Override
    public List<AuditLogResponse> getLogs(UUID storeId) {
        return auditLogRepository.findByStore_IdOrderByCreatedAtDesc(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByUser(UUID userId) {
        return auditLogRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .userName(log.getUser() != null ? log.getUser().getFirstName() + " " + log.getUser().getLastName() : "System")
                .storeId(log.getStore().getId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
