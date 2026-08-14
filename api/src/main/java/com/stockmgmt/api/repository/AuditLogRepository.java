package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByStore_IdOrderByCreatedAtDesc(UUID storeId);
    List<AuditLog> findByUser_IdOrderByCreatedAtDesc(UUID userId);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);
}
