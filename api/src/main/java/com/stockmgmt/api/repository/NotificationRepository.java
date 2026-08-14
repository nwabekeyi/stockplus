package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByStore_IdOrderByCreatedAtDesc(UUID storeId);
}
