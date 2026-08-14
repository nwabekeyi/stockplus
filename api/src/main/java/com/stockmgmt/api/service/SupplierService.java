package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateSupplierRequest;
import com.stockmgmt.api.entity.dto.response.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface SupplierService {
    SupplierResponse createSupplier(UUID storeId, CreateSupplierRequest request);
    List<SupplierResponse> getSuppliers(UUID storeId);
    SupplierResponse getSupplier(UUID storeId, UUID supplierId);
    SupplierResponse updateSupplier(UUID storeId, UUID supplierId, CreateSupplierRequest request);
    void deleteSupplier(UUID storeId, UUID supplierId);
}
