package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.Customer;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.CreateCustomerRequest;
import com.stockmgmt.api.entity.dto.response.CustomerResponse;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.CustomerRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.service.AuditLogService;
import com.stockmgmt.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CustomerResponse createCustomer(UUID storeId, CreateCustomerRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : java.math.BigDecimal.ZERO)
                .status(request.getStatus() != null ? request.getStatus() : com.stockmgmt.api.entity.enumeration.CustomerStatus.ACTIVE)
                .store(store)
                .build();

        customerRepository.save(customer);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditLogService.createLog(storeId, "CREATE_CUSTOMER", "Customer", customer.getId().toString(), null, null, user.getId());

        return mapToResponse(customer);
    }

    @Override
    public List<CustomerResponse> getCustomers(UUID storeId) {
        return customerRepository.findByStore_Id(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomer(UUID storeId, UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (!customer.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Customer does not belong to this store");
        }
        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(UUID storeId, UUID customerId, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (!customer.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Customer does not belong to this store");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        if (request.getCreditLimit() != null) {
            customer.setCreditLimit(request.getCreditLimit());
        }
        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }

        customerRepository.save(customer);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditLogService.createLog(storeId, "UPDATE_CUSTOMER", "Customer", customer.getId().toString(), null, null, user.getId());

        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID storeId, UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (!customer.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Customer does not belong to this store");
        }

        customerRepository.delete(customer);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditLogService.createLog(storeId, "DELETE_CUSTOMER", "Customer", customer.getId().toString(), null, null, user.getId());
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .creditLimit(customer.getCreditLimit())
                .outstandingBalance(customer.getOutstandingBalance())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
