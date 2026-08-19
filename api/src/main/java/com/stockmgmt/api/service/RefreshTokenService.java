package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    String generateRefreshToken(User user);

    Optional<User> validateRefreshToken(String token);

    void revokeRefreshToken(String token);

    void revokeAllUserRefreshTokens(User user);
}
