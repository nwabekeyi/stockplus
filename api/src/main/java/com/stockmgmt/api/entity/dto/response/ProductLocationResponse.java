package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.ProductLocationType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductLocationResponse {
    private UUID id;
    private ProductLocationType locationType;
    private String locationName;
    private int quantity;
}
