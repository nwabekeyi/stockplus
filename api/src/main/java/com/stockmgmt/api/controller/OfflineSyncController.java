package com.stockmgmt.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmgmt.api.entity.OfflineSyncItem;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.Subscription;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.OfflineMutationRequest;
import com.stockmgmt.api.entity.dto.request.OfflineSyncRequest;
import com.stockmgmt.api.entity.dto.response.OfflineMutationResult;
import com.stockmgmt.api.entity.dto.response.OfflineSyncResponse;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.OfflineSyncItemRepository;
import com.stockmgmt.api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offline-sync")
@RequiredArgsConstructor
public class OfflineSyncController {
    private final OfflineSyncItemRepository offlineSyncItemRepository;
    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/{storeId}")
    public ResponseEntity<OfflineSyncResponse> syncMutations(@PathVariable UUID storeId,
                                                             @RequestBody OfflineSyncRequest request,
                                                             @AuthenticationPrincipal User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        ensureCloudSubscription(store);

        List<OfflineMutationResult> results = new ArrayList<>();
        int accepted = 0;
        int rejected = 0;

        for (OfflineMutationRequest mutation : request.getMutations()) {
            try {
                if (mutation.getClientMutationId() == null || mutation.getClientMutationId().isBlank()) {
                    throw new IllegalArgumentException("clientMutationId is required");
                }
                if (offlineSyncItemRepository.findByClientMutationId(mutation.getClientMutationId()).isPresent()) {
                    results.add(OfflineMutationResult.builder()
                            .clientMutationId(mutation.getClientMutationId())
                            .status("duplicate")
                            .build());
                    continue;
                }

                offlineSyncItemRepository.save(OfflineSyncItem.builder()
                        .store(store)
                        .user(user)
                        .clientMutationId(mutation.getClientMutationId())
                        .method(mutation.getMethod())
                        .endpoint(mutation.getEndpoint())
                        .payload(objectMapper.writeValueAsString(mutation.getBody()))
                        .status("ACCEPTED")
                        .processedAt(LocalDateTime.now())
                        .build());
                accepted++;
                results.add(OfflineMutationResult.builder()
                        .clientMutationId(mutation.getClientMutationId())
                        .status("accepted")
                        .build());
            } catch (Exception ex) {
                rejected++;
                results.add(OfflineMutationResult.builder()
                        .clientMutationId(mutation.getClientMutationId())
                        .status("rejected")
                        .error(ex.getMessage())
                        .build());
            }
        }

        return ResponseEntity.ok(OfflineSyncResponse.builder()
                .accepted(accepted)
                .rejected(rejected)
                .results(results)
                .build());
    }

    private void ensureCloudSubscription(Store store) {
        Subscription subscription = store.getSubscription();
        if (subscription == null || subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "An active subscription is required to sync offline data to the backend database");
        }
    }
}
