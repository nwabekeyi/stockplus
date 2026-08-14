package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StoreResponse {
    private UUID id;
    private String name;
    private String logo;
    private String addressNumber;
    private String addressStreet;
    private String addressArea;
    private String addressLga;
    private String addressState;
    private String addressCountry;
    private String phoneNumber;
    private String contactInfo;
    private String operatingHours;
    private String taxNumber;
    private String currency;
    private boolean active;
}
