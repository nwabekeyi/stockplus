package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByStore_Id(UUID storeId);
    java.util.List<Subscription> findByStatus(com.stockmgmt.api.entity.enumeration.SubscriptionStatus status);
}