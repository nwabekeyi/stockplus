package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStore_Id(UUID storeId);

    Optional<Product> findByStore_IdAndSku(UUID storeId, String sku);

    boolean existsByStore_IdAndSku(UUID storeId, String sku);

    long countByStore_Id(UUID storeId);

    List<Product> findByStore_IdAndArchived(UUID storeId, boolean archived);

    List<Product> findByStore_IdAndCategoryId(UUID storeId, UUID categoryId);

    List<Product> findByStore_IdAndActiveAndArchived(UUID storeId, boolean active, boolean archived);

    @Query("""
            SELECT p FROM Product p
            WHERE p.store.id = :storeId
            AND p.archived = false
            AND (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """)
    List<Product> search(@Param("storeId") UUID storeId, @Param("query") String query);
}
