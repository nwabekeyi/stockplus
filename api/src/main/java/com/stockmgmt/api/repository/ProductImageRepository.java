package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProduct_IdOrderBySortOrderAsc(UUID productId);
}
