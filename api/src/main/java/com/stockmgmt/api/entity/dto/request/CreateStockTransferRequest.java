package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.TransferStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStockTransferRequest {
    @NotBlank
    private String reference;

    @NotNull
    private UUID fromStoreId;

    @NotNull
    private UUID toStoreId;

    @NotNull
    private UUID productId;

    @NotNull
    private int quantity;

    private TransferStatus status;

    private String notes;
}
