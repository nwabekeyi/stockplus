package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.OfflineSyncItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfflineSyncItemRepository extends JpaRepository<OfflineSyncItem, UUID> {
    Optional<OfflineSyncItem> findByClientMutationId(String clientMutationId);
    List<OfflineSyncItem> findByStore_IdOrderByCreatedAtDesc(UUID storeId);
}
