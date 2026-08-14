package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.Branch;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.request.CreateBranchRequest;
import com.stockmgmt.api.entity.dto.response.BranchResponse;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.BranchRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.service.AuditLogService;
import com.stockmgmt.api.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final StoreRepository storeRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public BranchResponse createBranch(UUID storeId, CreateBranchRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        if (branchRepository.existsByStore_IdAndName(storeId, request.getName())) {
            throw new RuntimeException("Branch with this name already exists");
        }

        Branch branch = Branch.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .manager(request.getManager())
                .active(true)
                .store(store)
                .build();

        branchRepository.save(branch);
        return mapToResponse(branch);
    }

    @Override
    public List<BranchResponse> getBranches(UUID storeId) {
        return branchRepository.findByStore_Id(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BranchResponse getBranch(UUID storeId, UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        if (!branch.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Branch does not belong to this store");
        }
        return mapToResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(UUID storeId, UUID branchId, CreateBranchRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        if (!branch.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Branch does not belong to this store");
        }

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setManager(request.getManager());

        branchRepository.save(branch);
        return mapToResponse(branch);
    }

    @Override
    @Transactional
    public void deleteBranch(UUID storeId, UUID branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        if (!branch.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Branch does not belong to this store");
        }
        branchRepository.delete(branch);
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .manager(branch.getManager())
                .active(branch.isActive())
                .createdAt(branch.getCreatedAt())
                .storeId(branch.getStore().getId())
                .build();
    }
}
