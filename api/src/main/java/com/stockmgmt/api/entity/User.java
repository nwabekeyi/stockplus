package com.stockmgmt.api.entity;

import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.entity.enumeration.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @UuidGenerator(style = org.hibernate.annotations.UuidGenerator.Style.VERSION_7)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String phoneNumber;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Store store;

    @Transient
    public Subscription getSubscription() {
        if (store != null && store.getSubscription() != null) {
            return store.getSubscription();
        }
        return null;
    }

    @Transient
    public SubscriptionPlan getSubscriptionPlan() {
        Subscription subscription = getSubscription();
        if (subscription != null && subscription.getPlan() != null) {
            return subscription.getPlan();
        }
        return null;
    }

    @Transient
    public boolean hasActiveSubscription() {
        Subscription subscription = getSubscription();
        return subscription != null && subscription.getStatus() == SubscriptionStatus.ACTIVE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}