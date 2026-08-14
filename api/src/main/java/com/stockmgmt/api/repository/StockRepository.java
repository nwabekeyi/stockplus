package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByProduct_Id(UUID productId);
    java.util.List<Stock> findByQuantityLessThanEqual(int quantity);
}