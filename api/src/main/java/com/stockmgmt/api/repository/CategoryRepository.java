package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    java.util.List<Category> findByStore_Id(UUID storeId);
    Optional<Category> findByStore_IdAndName(UUID storeId, String name);
}