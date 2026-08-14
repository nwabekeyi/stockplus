package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.WholesalePriceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WholesalePriceRuleRepository extends JpaRepository<WholesalePriceRule, UUID> {
    List<WholesalePriceRule> findByProduct_IdOrderByMinQuantityAsc(UUID productId);
    void deleteByProduct_Id(UUID productId);
}
