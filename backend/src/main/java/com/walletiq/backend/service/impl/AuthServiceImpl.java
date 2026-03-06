package com.walletiq.backend.service.impl;

import com.walletiq.backend.config.properties.JwtSecurityProperties;
import com.walletiq.backend.entity.RefreshToken;
import com.walletiq.backend.entity.User;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.AuthException;
import com.walletiq.backend.payload.request.LoginRequest;
import com.walletiq.backend.payload.request.LogoutRequest;
import com.walletiq.backend.payload.request.SignupRequest;
import com.walletiq.backend.payload.response.AuthResponse;
import com.walletiq.backend.payload.response.TokenResponse;
import com.walletiq.backend.repository.RefreshTokenRepository;
import com.walletiq.backend.repository.UserRepository;
import com.walletiq.backend.security.JwtService;
import com.walletiq.backend.service.AuthService;
import com.walletiq.backend.util.TimeConversionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtSecurityProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("Signup attempt for email: {}", request.email());

        // Check if user already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        var tokens = issueTokens(user);
        return AuthResponse.of(user, tokens);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Invalid password attempt for email: {}", request.email());
            // Same exception as user-not-found — never leak which one failed
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }

        log.info("User logged in successfully: {}", user.getId());

        var tokens = issueTokens(user);
        return AuthResponse.of(user, tokens);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Refresh attempt for refreshToken: {}", refreshToken);

        // Validate the token structure and signature
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AuthException(ErrorCode.TOKEN_INVALID);
        }

        // Must be a refresh token — reject access tokens
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new AuthException(ErrorCode.TOKEN_INVALID,
                "Access tokens cannot be used to refresh sessions");
        }

        // Find in DB
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new AuthException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // Check if already revoked
        if (stored.isRevoked()) {
            log.warn("Revoked refresh token used for user: {}", stored.getUser().getId());
            throw new AuthException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if (stored.isExpired()) {
            log.warn("Expired refresh token used for user: {}", stored.getUser().getId());
            throw new AuthException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = stored.getUser();

        // Revoke old token
        stored.revoke(Instant.now());
        refreshTokenRepository.save(stored);

        // Issue new pair (rotation)
        TokenResponse tokens = issueTokens(user);

        log.info("Token refreshed successfully for user: {}", user.getId());
        return tokens;
    }

    @Override
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new AuthException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (token.isRevoked()) {
            log.warn("Logout called with already-revoked token for user: {}",
                token.getUser().getId());
            return;
        }

        token.revoke(Instant.now());
        refreshTokenRepository.save(token);

        log.info("User logged out successfully: {}", token.getUser().getId());
    }

    // Helpers

    /**
     * Revokes any existing refresh token for the user, issues a new access
     * and refresh token pair, and persists the new refresh token.
     * Called on both signup and login — enforces the one-active-refresh-token rule.
     */
    private TokenResponse issueTokens(User user) {
        String userId = user.getId().toString();
        String email = user.getEmail();

        // Revoke existing refresh token if present — one active token per user
        refreshTokenRepository.findActiveByUserId(user.getId(), Instant.now())
            .ifPresent(existing -> {
                existing.revoke(Instant.now());
                refreshTokenRepository.save(existing);
                log.debug("Revoked existing refresh token for user: {}", userId);
            });

        // Issue new tokens
        String accessToken = jwtService.generateAccessToken(userId, email);
        String refreshToken = jwtService.generateRefreshToken(userId, email);

        // Persist the new refresh token
        Instant now = Instant.now();
        long expiresIn = TimeConversionUtil.millisToSeconds(jwtProperties.accessToken().expiry());

        RefreshToken entity = new RefreshToken();
        entity.setToken(refreshToken);
        entity.setUser(user);
        entity.setCreatedAt(now);
        entity.setExpiresAt(now.plusSeconds(
            TimeConversionUtil.millisToSeconds(jwtProperties.refreshToken().expiry())
        ));
        entity.setRevoked(false);
        refreshTokenRepository.save(entity);

        return TokenResponse.of(accessToken, refreshToken, expiresIn);
    }
}
