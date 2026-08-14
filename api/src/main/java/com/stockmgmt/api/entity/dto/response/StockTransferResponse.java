package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.TransferStatus;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockTransferResponse {
    private UUID id;
    private String reference;
    private UUID fromStoreId;
    private UUID toStoreId;
    private UUID productId;
    private String productName;
    private int quantity;
    private TransferStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;
}
