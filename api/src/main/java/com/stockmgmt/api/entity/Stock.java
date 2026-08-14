package com.stockmgmt.api.entity;

import com.stockmgmt.api.entity.enumeration.UnitOfMeasure;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(nullable = false)
    private int quantity = 0;

    @Column(nullable = false)
    private int lowStockThreshold = 10;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UnitOfMeasure unit = UnitOfMeasure.PIECE;

    @Column(nullable = false)
    private boolean trackInventory = true;

    @Column
    private String batchNumber;

    @Column
    private LocalDate expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private int minStockLevel = 0;

    @Column
    private Integer maxStockLevel;
}