package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
}