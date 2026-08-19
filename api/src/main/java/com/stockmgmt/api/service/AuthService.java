package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.LoginRequest;
import com.stockmgmt.api.entity.dto.request.RegisterRequest;
import com.stockmgmt.api.entity.dto.request.UpdateProfileRequest;
import com.stockmgmt.api.entity.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request, HttpServletResponse response);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    AuthResponse updateProfile(UpdateProfileRequest request);
}
