package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateSupplierRequest;
import com.stockmgmt.api.entity.dto.response.SupplierResponse;
import com.stockmgmt.api.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/stores/{storeId}/suppliers")
    public ResponseEntity<SupplierResponse> createSupplier(@PathVariable UUID storeId,
                                                           @Valid @RequestBody CreateSupplierRequest request) {
        return ResponseEntity.ok(supplierService.createSupplier(storeId, request));
    }

    @GetMapping("/stores/{storeId}/suppliers")
    public ResponseEntity<List<SupplierResponse>> getSuppliers(@PathVariable UUID storeId) {
        return ResponseEntity.ok(supplierService.getSuppliers(storeId));
    }

    @GetMapping("/stores/{storeId}/suppliers/{supplierId}")
    public ResponseEntity<SupplierResponse> getSupplier(@PathVariable UUID storeId, @PathVariable UUID supplierId) {
        return ResponseEntity.ok(supplierService.getSupplier(storeId, supplierId));
    }

    @PutMapping("/stores/{storeId}/suppliers/{supplierId}")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable UUID storeId, @PathVariable UUID supplierId,
                                                           @Valid @RequestBody CreateSupplierRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(storeId, supplierId, request));
    }

    @DeleteMapping("/stores/{storeId}/suppliers/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID storeId, @PathVariable UUID supplierId) {
        supplierService.deleteSupplier(storeId, supplierId);
        return ResponseEntity.ok().build();
    }
}
