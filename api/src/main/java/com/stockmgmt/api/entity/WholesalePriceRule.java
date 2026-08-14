package com.stockmgmt.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wholesale_price_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WholesalePriceRule {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(nullable = false)
    private int minQuantity;

    @Column
    private Integer maxQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
