package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID> {
    List<StockTransfer> findByFromStore_IdOrToStoreId(UUID fromStoreId, UUID toStoreId);
    Optional<StockTransfer> findByReference(String reference);
    List<StockTransfer> findByStatus(com.stockmgmt.api.entity.enumeration.TransferStatus status);
}
