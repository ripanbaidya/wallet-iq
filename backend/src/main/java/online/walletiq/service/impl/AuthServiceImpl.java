package online.walletiq.service.impl;

import online.walletiq.config.properties.JwtSecurityProperties;
import online.walletiq.dto.auth.AuthResponse;
import online.walletiq.dto.auth.LoginRequest;
import online.walletiq.dto.auth.SignupRequest;
import online.walletiq.dto.auth.TokenResponse;
import online.walletiq.entity.RefreshToken;
import online.walletiq.entity.User;
import online.walletiq.enums.ErrorCode;
import online.walletiq.exception.AuthException;
import online.walletiq.repository.RefreshTokenRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.security.JwtService;
import online.walletiq.service.AuthService;
import online.walletiq.util.MaskingUtil;
import online.walletiq.util.TimeConversionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Default implementation of {@link AuthService}.
 * <p>Handles authentication operations such as signup, login, refresh token
 * rotation, and logout using JWT-based sessions.
 * <p>Refresh tokens are stored in the database to support revocation and
 * expiration validation.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtSecurityProperties jwtProperties;

    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void signup(SignupRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        log.debug("User registered successfully!");
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String maskedEmail = MaskingUtil.maskEmail(request.email());

        log.debug("Login attempt for email: {}", maskedEmail);

        // Check if user exists with the email
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS));

        // Check whether password matches
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Invalid password attempt for email: {}", maskedEmail);
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }

        var tokens = issueTokens(user);

        log.debug("User logged in successfully for id: {}", user.getId());
        return AuthResponse.of(user, tokens);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        log.debug("Refresh token attempt..");

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

        // Note: For refresh token we won't generate new pair (access + refresh)
        // Rather we are generating the access token only, and will send old refresh token
        String userId = user.getId().toString();
        String email = user.getEmail();

        String newAccessToken = jwtService.generateAccessToken(userId, email);
        long expiresIn = TimeConversionUtil.millisToSeconds(jwtProperties
            .accessToken().expiry());

        log.info("Token refreshed successfully for user: {}", user.getId());
        return TokenResponse.of(newAccessToken, refreshToken, expiresIn);
    }

    // TODO - Token blacklisting using Redis
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

    @Override
    public String getPasswordHash(String password) {
        return passwordEncoder.encode(password);
    }

    // Helper methods

    /**
     * Generates a new access and refresh token pair for the user.
     * <p>Ensures that only one active refresh token exists by revoking any
     * previously issued token before creating a new one.
     * The new refresh token is persisted with its expiration time.
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
