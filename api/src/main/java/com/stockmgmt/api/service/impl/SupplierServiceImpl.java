package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.Supplier;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.request.CreateSupplierRequest;
import com.stockmgmt.api.entity.dto.response.SupplierResponse;
import com.stockmgmt.api.entity.enumeration.SupplierStatus;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.SupplierRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.service.AuditLogService;
import com.stockmgmt.api.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final StoreRepository storeRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SupplierResponse createSupplier(UUID storeId, CreateSupplierRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .status(request.getStatus() != null ? request.getStatus() : SupplierStatus.ACTIVE)
                .store(store)
                .build();

        supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getSuppliers(UUID storeId) {
        return supplierRepository.findByStore_Id(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SupplierResponse getSupplier(UUID storeId, UUID supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        if (!supplier.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Supplier does not belong to this store");
        }
        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(UUID storeId, UUID supplierId, CreateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        if (!supplier.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Supplier does not belong to this store");
        }

        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        }

        supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(UUID storeId, UUID supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        if (!supplier.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Supplier does not belong to this store");
        }
        supplierRepository.delete(supplier);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .outstandingBalance(supplier.getOutstandingBalance())
                .status(supplier.getStatus())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
