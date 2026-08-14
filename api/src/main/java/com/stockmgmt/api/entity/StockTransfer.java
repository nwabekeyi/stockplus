package com.stockmgmt.api.entity;

import com.stockmgmt.api.entity.enumeration.TransferStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransfer {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "from_store_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Store fromStore;

    @ManyToOne
    @JoinColumn(name = "to_store_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Store toStore;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status = TransferStatus.PENDING;

    @Column
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime receivedAt;
}
