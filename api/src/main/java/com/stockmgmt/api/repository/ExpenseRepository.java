package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByStore_Id(UUID storeId);
    List<Expense> findByStore_IdAndExpenseDateBetween(UUID storeId, LocalDateTime start, LocalDateTime end);
    List<Expense> findByStore_IdAndCategory(UUID storeId, com.stockmgmt.api.entity.enumeration.ExpenseCategory category);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.store.id = :storeId AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumAmountByStoreIdAndExpenseDateBetween(@Param("storeId") UUID storeId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
