package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateCustomerRequest;
import com.stockmgmt.api.entity.dto.response.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(UUID storeId, CreateCustomerRequest request);
    List<CustomerResponse> getCustomers(UUID storeId);
    CustomerResponse getCustomer(UUID storeId, UUID customerId);
    CustomerResponse updateCustomer(UUID storeId, UUID customerId, CreateCustomerRequest request);
    void deleteCustomer(UUID storeId, UUID customerId);
}
