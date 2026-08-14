package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.UnitOfMeasure;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class StockResponse {
    private UUID id;
    private int quantity;
    private int lowStockThreshold;
    private UnitOfMeasure unit;
    private boolean trackInventory;
    private String batchNumber;
    private LocalDate expiryDate;
    private int minStockLevel;
    private Integer maxStockLevel;
}
