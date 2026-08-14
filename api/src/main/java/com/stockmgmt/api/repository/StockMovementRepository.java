package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByStore_Id(UUID storeId);
    List<StockMovement> findByProduct_Id(UUID productId);
    List<StockMovement> findByStore_IdAndMovementType(UUID storeId, com.stockmgmt.api.entity.enumeration.MovementType movementType);
    List<StockMovement> findByStore_IdOrderByCreatedAtDesc(UUID storeId);
}
