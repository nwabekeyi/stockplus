package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findByStore_Id(UUID storeId);
    List<Supplier> findByStore_IdAndStatus(UUID storeId, com.stockmgmt.api.entity.enumeration.SupplierStatus status);
}
