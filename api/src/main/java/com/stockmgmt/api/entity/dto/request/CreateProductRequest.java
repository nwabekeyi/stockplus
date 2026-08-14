package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Selling price is required")
    private BigDecimal sellingPrice;

    @NotNull(message = "Cost price is required")
    private BigDecimal costPrice;

    private BigDecimal wholesalePrice;

    private String sku;

    private String barcode;

    private String image;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    private UUID supplierId;

    private String batchNumber;

    private LocalDate expiryDate;

    private Boolean active;

    private Integer minStockLevel;

    private Integer maxStockLevel;

    private Integer initialQuantity;

    private Integer lowStockThreshold;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unit;

    private Boolean trackInventory;

    private List<WholesaleRuleRequest> wholesaleRules;

    @Data
    @Builder
    public static class WholesaleRuleRequest {
        @NotNull(message = "Min quantity is required")
        private Integer minQuantity;

        private Integer maxQuantity;

        @NotNull(message = "Price is required")
        private BigDecimal price;
    }
}
