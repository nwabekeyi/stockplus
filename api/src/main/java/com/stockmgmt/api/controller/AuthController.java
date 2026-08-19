package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.LoginRequest;
import com.stockmgmt.api.entity.dto.request.RegisterRequest;
import com.stockmgmt.api.entity.dto.request.UpdateProfileRequest;
import com.stockmgmt.api.entity.dto.response.AuthResponse;
import com.stockmgmt.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request,
                                                 HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || isAnonymous(auth)) {
            return ResponseEntity.status(401).build();
        }
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

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }

    private boolean isAnonymous(Authentication auth) {
        Object principal = auth.getPrincipal();
        return principal instanceof String && "anonymousUser".equals(principal);
    }
}
