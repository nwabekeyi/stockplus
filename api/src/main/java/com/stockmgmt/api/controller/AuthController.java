package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.LoginRequest;
import com.stockmgmt.api.entity.dto.request.RegisterRequest;
import com.stockmgmt.api.entity.dto.response.AuthResponse;
import com.stockmgmt.api.entity.enumeration.UserRole;
import com.stockmgmt.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("X-Refresh-Token");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        AuthResponse response = AuthResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .hasStore(user.getStore() != null)
            .storeId(user.getStore() != null ? user.getStore().getId() : null)
            .storeName(user.getStore() != null ? user.getStore().getName() : null)
            .storeCurrency(user.getStore() != null ? user.getStore().getCurrency() : null)
            .build();
            
        return ResponseEntity.ok(response);
    }
}