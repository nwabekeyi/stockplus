package com.stockmgmt.api.entity;

import com.stockmgmt.api.entity.enumeration.ProductLocationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLocation {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductLocationType locationType;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 0;
}
