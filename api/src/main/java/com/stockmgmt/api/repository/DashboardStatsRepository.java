package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.DashboardStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DashboardStatsRepository extends JpaRepository<DashboardStats, UUID> {
    Optional<DashboardStats> findByStoreId(UUID storeId);
}
