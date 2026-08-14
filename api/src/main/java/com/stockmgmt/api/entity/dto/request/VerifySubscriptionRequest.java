package com.stockmgmt.api.entity.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifySubscriptionRequest {
    private String reference;
    private String authorizationCode;
}