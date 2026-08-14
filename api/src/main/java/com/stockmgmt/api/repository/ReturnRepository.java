package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReturnRepository extends JpaRepository<Return, UUID> {
    List<Return> findByStore_IdOrderByCreatedAtDesc(UUID storeId);
}
