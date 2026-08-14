package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.LoginRequest;
import com.stockmgmt.api.entity.dto.request.RegisterRequest;
import com.stockmgmt.api.entity.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout();
}