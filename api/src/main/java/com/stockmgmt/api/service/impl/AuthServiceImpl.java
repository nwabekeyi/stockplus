package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.LoginRequest;
import com.stockmgmt.api.entity.dto.request.RegisterRequest;
import com.stockmgmt.api.entity.dto.request.UpdateProfileRequest;
import com.stockmgmt.api.entity.dto.response.AuthResponse;
import com.stockmgmt.api.entity.enumeration.UserRole;
import com.stockmgmt.api.exception.InvalidRefreshTokenException;
import com.stockmgmt.api.repository.UserRepository;
import com.stockmgmt.api.security.CookieService;
import com.stockmgmt.api.security.JwtService;
import com.stockmgmt.api.service.AuthService;
import com.stockmgmt.api.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;
    private final AuthenticationManager authenticationManager;

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
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
    }

    private void issueTokens(HttpServletResponse response, User user) {
        UserDetails userDetails = toUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(user);

        cookieService.addAccessTokenCookie(response, accessToken);
        cookieService.addRefreshTokenCookie(response, refreshToken);
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", ""))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(UserRole.ROLE_USER)
                .active(true)
                .build();

        userRepository.save(user);

        issueTokens(response, user);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        issueTokens(response, user);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookie(request);

        return refreshTokenService.validateRefreshToken(refreshToken)
                .map(user -> {
                    issueTokens(response, user);
                    return buildAuthResponse(user);
                })
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token"));
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookie(request);
        refreshTokenService.validateRefreshToken(refreshToken)
                .ifPresentOrElse(
                        user -> refreshTokenService.revokeAllUserRefreshTokens(user),
                        () -> refreshTokenService.revokeRefreshToken(refreshToken)
                );
        cookieService.clearAllCookies(response);
    }

    @Override
    @Transactional
    public AuthResponse updateProfile(UpdateProfileRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);

        return buildAuthResponse(user);
    }
}
