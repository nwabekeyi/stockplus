package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.ProductLocationType;
import com.stockmgmt.api.entity.enumeration.UnitOfMeasure;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private BigDecimal wholesalePrice;
    private String sku;
    private String barcode;
    private boolean active;
    private boolean archived;
    private int minStockLevel;
    private Integer maxStockLevel;
    private String image;
    private String batchNumber;
    private LocalDate expiryDate;
    private CategoryResponse category;
    private SupplierResponse supplier;
    private StockResponse stock;
    private List<ProductImageResponse> images;
    private List<ProductLocationResponse> locations;
    private List<WholesalePriceRuleResponse> wholesaleRules;
}
