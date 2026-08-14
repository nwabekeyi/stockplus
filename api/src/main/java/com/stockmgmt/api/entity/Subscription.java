package com.stockmgmt.api.entity;

import com.stockmgmt.api.entity.enumeration.PaymentStatus;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "store_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Store store;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SubscriptionPlan plan;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column
    private String paystackSubscriptionCode;

    @Column
    private String paystackAuthorizationCode;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean autoRenew = true;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private String cancellationReason;
}