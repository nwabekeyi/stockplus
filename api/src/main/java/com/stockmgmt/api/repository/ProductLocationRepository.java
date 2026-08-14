package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.ProductLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductLocationRepository extends JpaRepository<ProductLocation, UUID> {
    List<ProductLocation> findByProduct_Id(UUID productId);
}
