package com.stockmgmt.api.repository;

import com.stockmgmt.api.entity.RefreshToken;
import com.stockmgmt.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserId(UUID userId);

    List<RefreshToken> findAllByUserIdAndRevokedFalseAndExpiryDateAfter(UUID userId, java.time.Instant now);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user = :user")
    int revokeAllByUserId(User user);
}
