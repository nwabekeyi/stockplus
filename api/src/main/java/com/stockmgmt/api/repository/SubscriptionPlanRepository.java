package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    Optional<SubscriptionPlan> findByName(String name);
    java.util.List<SubscriptionPlan> findByActiveTrue();
}