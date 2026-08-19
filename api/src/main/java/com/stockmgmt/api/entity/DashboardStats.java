package com.stockmgmt.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dashboard_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID storeId;

    @Column
    private int totalProducts;

    @Column
    private int lowStockCount;

    @Column
    private int totalSalesToday;

    @Column
    private BigDecimal revenueToday;

    @Column
    private BigDecimal revenueThisMonth;

    @Column
    private int totalSalesThisMonth;

    @Column
    private BigDecimal customerDebt;

    @Column
    private BigDecimal supplierDebt;

    @Column
    private BigDecimal expensesToday;

    @Column
    private int totalCustomers;

    @Column
    private int totalSuppliers;

    @Column
    private Instant lastUpdated;
}
