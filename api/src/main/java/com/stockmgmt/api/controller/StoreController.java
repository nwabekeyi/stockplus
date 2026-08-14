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
}
