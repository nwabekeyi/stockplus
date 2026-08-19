package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.StoreWorker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreWorkerRepository extends JpaRepository<StoreWorker, UUID> {
    long countByStore_IdAndActiveTrue(UUID storeId);
    List<StoreWorker> findByStore_IdOrderByCreatedAtDesc(UUID storeId);
    Optional<StoreWorker> findByStore_IdAndEmail(UUID storeId, String email);
}
