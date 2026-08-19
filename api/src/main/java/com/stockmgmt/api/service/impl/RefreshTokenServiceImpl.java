package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.config.AppProperties;
import com.stockmgmt.api.entity.RefreshToken;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.repository.RefreshTokenRepository;
import com.stockmgmt.api.security.JwtService;
import com.stockmgmt.api.service.RefreshTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private final AppProperties appProperties;

    @Override
    public String generateRefreshToken(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenHash(hashToken(refreshToken))
                .user(user)
                .issuedAt(Instant.now())
                .expiryDate(Instant.now().plusMillis(appProperties.getJwt().getRefreshExpiration()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }

    @Override
    public Optional<User> validateRefreshToken(String token) {
        if (token == null) {
            return Optional.empty();
        }

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (JwtException e) {
            log.debug("Refresh token signature/parse invalid: {}", e.getMessage());
            return Optional.empty();
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            return Optional.empty();
        }
        if (!jwtService.isRefreshTokenValid(token, userDetails)) {
            return Optional.empty();
        }

        String tokenHash = hashToken(token);
        Optional<RefreshToken> stored = refreshTokenRepository.findByTokenHash(tokenHash);
        if (stored.isEmpty() || stored.get().isRevoked() || stored.get().getExpiryDate().isBefore(Instant.now())) {
            return Optional.empty();
        }

        return Optional.of((User) userDetails);
    }

    @Override
    public void revokeRefreshToken(String token) {
        if (token == null) {
            return;
        }
        String tokenHash = hashToken(token);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    @Override
    @Transactional
    public void revokeAllUserRefreshTokens(User user) {
        refreshTokenRepository.revokeAllByUserId(user);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 digest not available", e);
        }
    }
}

