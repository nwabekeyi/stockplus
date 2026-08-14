package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateCustomerRequest;
import com.stockmgmt.api.entity.dto.response.CustomerResponse;
import com.stockmgmt.api.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/stores/{storeId}/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@PathVariable UUID storeId,
                                                           @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(storeId, request));
    }

    @GetMapping("/stores/{storeId}/customers")
    public ResponseEntity<List<CustomerResponse>> getCustomers(@PathVariable UUID storeId) {
        return ResponseEntity.ok(customerService.getCustomers(storeId));
    }

    @GetMapping("/stores/{storeId}/customers/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID storeId, @PathVariable UUID customerId) {
        return ResponseEntity.ok(customerService.getCustomer(storeId, customerId));
    }

    @PutMapping("/stores/{storeId}/customers/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID storeId, @PathVariable UUID customerId,
                                                           @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(storeId, customerId, request));
    }

    @DeleteMapping("/stores/{storeId}/customers/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID storeId, @PathVariable UUID customerId) {
        customerService.deleteCustomer(storeId, customerId);
        return ResponseEntity.ok().build();
    }
}
