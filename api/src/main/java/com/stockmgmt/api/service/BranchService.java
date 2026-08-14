package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateBranchRequest;
import com.stockmgmt.api.entity.dto.response.BranchResponse;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchResponse createBranch(UUID storeId, CreateBranchRequest request);
    List<BranchResponse> getBranches(UUID storeId);
    BranchResponse getBranch(UUID storeId, UUID branchId);
    BranchResponse updateBranch(UUID storeId, UUID branchId, CreateBranchRequest request);
    void deleteBranch(UUID storeId, UUID branchId);
}
