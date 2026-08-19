package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateStoreRequest;
import com.stockmgmt.api.entity.dto.response.StoreResponse;
import com.stockmgmt.api.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.Store;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        StoreResponse store = storeService.createStoreForUser(user.getId(), request);
        return ResponseEntity.ok(store);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable UUID storeId) {
        Store store = storeService.getStore(storeId);
        return ResponseEntity.ok(StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .logo(store.getLogo())
                .addressNumber(store.getAddressNumber())
                .addressStreet(store.getAddressStreet())
                .addressArea(store.getAddressArea())
                .addressLga(store.getAddressLga())
                .addressState(store.getAddressState())
                .addressCountry(store.getAddressCountry())
                .phoneNumber(store.getPhoneNumber())
                .contactInfo(store.getContactInfo())
                .operatingHours(store.getOperatingHours())
                .operatingDaysFrom(store.getOperatingDaysFrom())
                .operatingDaysTo(store.getOperatingDaysTo())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .taxNumber(store.getTaxNumber())
                .currency(store.getCurrency())
                .active(store.isActive())
                .build());
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponse> updateStore(@PathVariable UUID storeId,
                                                     @Valid @RequestBody CreateStoreRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Store store = storeService.getStore(storeId);
        if (!store.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        StoreResponse updated = storeService.updateStore(storeId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID storeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Store store = storeService.getStore(storeId);
        if (!store.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        storeService.deleteStore(storeId);
        return ResponseEntity.ok().build();
    }
}
