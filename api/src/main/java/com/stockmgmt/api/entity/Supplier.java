package com.stockmgmt.api.entity;

import com.stockmgmt.api.entity.enumeration.SupplierStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String phone;

    @Column
    private String email;

    @Column
    private String address;

    @Column(precision = 15, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SupplierStatus status = SupplierStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Store store;
}
