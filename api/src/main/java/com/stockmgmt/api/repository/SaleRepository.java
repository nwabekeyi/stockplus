package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByStore_IdOrderBySaleDateDesc(UUID storeId);
    List<Sale> findByStore_IdAndSaleDateBetween(UUID storeId, LocalDateTime start, LocalDateTime end);
}