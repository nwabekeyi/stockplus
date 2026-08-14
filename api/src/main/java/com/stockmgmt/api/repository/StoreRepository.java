package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findByOwner_Id(UUID ownerId);
    Optional<Store> findByName(String name);
}