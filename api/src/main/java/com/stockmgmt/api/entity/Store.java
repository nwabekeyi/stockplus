package com.stockmgmt.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String logo;

    @Column
    private String addressNumber;

    @Column
    private String addressStreet;

    @Column
    private String addressArea;

    @Column
    private String addressLga;

    @Column
    private String addressState;

    @Column
    private String addressCountry;

    @Column
    private String phoneNumber;

    @Column
    private String contactInfo;

    @Column
    private String operatingHours;

    @Column
    private String operatingDaysFrom;

    @Column
    private String operatingDaysTo;

    @Column
    private String openTime;

    @Column
    private String closeTime;

    @Column
    private String taxNumber;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "NGN";

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User owner;

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Subscription subscription;
}