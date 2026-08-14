package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateBranchRequest;
import com.stockmgmt.api.entity.dto.response.BranchResponse;
import com.stockmgmt.api.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/stores/{storeId}/branches")
    public ResponseEntity<BranchResponse> createBranch(@PathVariable UUID storeId,
                                                       @Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(branchService.createBranch(storeId, request));
    }

    @GetMapping("/stores/{storeId}/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(@PathVariable UUID storeId) {
        return ResponseEntity.ok(branchService.getBranches(storeId));
    }

    @GetMapping("/stores/{storeId}/branches/{branchId}")
    public ResponseEntity<BranchResponse> getBranch(@PathVariable UUID storeId, @PathVariable UUID branchId) {
        return ResponseEntity.ok(branchService.getBranch(storeId, branchId));
    }

    @PutMapping("/stores/{storeId}/branches/{branchId}")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable UUID storeId, @PathVariable UUID branchId,
                                                       @Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(storeId, branchId, request));
    }

    @DeleteMapping("/stores/{storeId}/branches/{branchId}")
    public ResponseEntity<Void> deleteBranch(@PathVariable UUID storeId, @PathVariable UUID branchId) {
        branchService.deleteBranch(storeId, branchId);
        return ResponseEntity.ok().build();
    }
}
