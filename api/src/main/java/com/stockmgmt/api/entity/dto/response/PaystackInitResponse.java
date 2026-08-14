package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaystackInitResponse {
    private String authorizationUrl;
    private String accessCode;
    private String reference;
}