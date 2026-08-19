package com.stockmgmt.api.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStoreRequest {
    @NotBlank
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

    private String operatingDaysFrom;

    private String operatingDaysTo;

    private String openTime;

    private String closeTime;

    private String taxNumber;

    private String currency;

    private UUID planId;

    private boolean offlineOnly;
}
