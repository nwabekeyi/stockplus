package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.CreateWorkerRequest;
import com.stockmgmt.api.entity.dto.response.WorkerLimitResponse;
import com.stockmgmt.api.entity.dto.response.WorkerResponse;
import com.stockmgmt.api.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping
    public ResponseEntity<List<WorkerResponse>> listWorkers(@PathVariable UUID storeId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workerService.listWorkers(storeId, user));
    }

    @GetMapping("/limits")
    public ResponseEntity<WorkerLimitResponse> getWorkerLimits(@PathVariable UUID storeId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workerService.getWorkerLimits(storeId, user));
    }

    @PostMapping
    public ResponseEntity<WorkerResponse> createWorker(@PathVariable UUID storeId,
                                                       @Valid @RequestBody CreateWorkerRequest request,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workerService.createWorker(storeId, request, user));
    }
}
