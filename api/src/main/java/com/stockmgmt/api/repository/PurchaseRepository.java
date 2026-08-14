package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    List<Purchase> findByStore_Id(UUID storeId);
    Optional<Purchase> findByStore_IdAndReference(UUID storeId, String reference);
    List<Purchase> findByStore_IdAndStatus(UUID storeId, com.stockmgmt.api.entity.enumeration.PurchaseStatus status);
}
