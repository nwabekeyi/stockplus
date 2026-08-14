package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    List<Branch> findByStore_Id(UUID storeId);
    boolean existsByStore_IdAndName(UUID storeId, String name);
}
