package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByStore_Id(UUID storeId);
    List<Customer> findByStore_IdAndStatus(UUID storeId, com.stockmgmt.api.entity.enumeration.CustomerStatus status);
}
