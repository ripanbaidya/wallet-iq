package com.walletiq.service;

import com.walletiq.dto.auth.LoginRequest;
import com.walletiq.dto.auth.SignupRequest;
import com.walletiq.dto.auth.AuthResponse;
import com.walletiq.dto.auth.TokenResponse;
import com.walletiq.exception.AuthException;

/**
 * Service responsible for authentication and session management.
 * <p>Provides operations for user registration, login, token refresh,
 * and logout. It also exposes a utility method for generating password hashes.
 * <p>The service issues JWT access and refresh tokens and manages
 * refresh token lifecycle including validation and revocation.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param request signup request containing user's name, email, and password
     * @throws AuthException if a user with the given email already exists
     */
    void signup(SignupRequest request);

    /**
     * Authenticates a user using email and password.
     * <p>If authentication is successful, an access token and refresh token
     * pair is issued and returned.
     *
     * @param request login request containing user credentials
     * @return authentication response containing user details and tokens
     * @throws AuthException if the email does not exist or the password is incorrect
     */
    AuthResponse login(LoginRequest request);

    /**
     * Generates a new access token using a valid refresh token.
     * <p>The provided refresh token is validated and revoked before issuing
     * a new access and refresh token pair (token rotation).
     *
     * @param refreshToken valid refresh token issued during login
     * @return new token response containing access and refresh tokens
     * @throws AuthException if the token is invalid, expired, revoked,
     *                       or not found in the database
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * Logs out a user by revoking the provided refresh token.
     * <p>If the token is already revoked, the method simply returns
     * without performing additional actions.
     *
     * @param refreshToken refresh token associated with the user's session
     * @throws AuthException if the refresh token does not exist
     */
    void logout(String refreshToken);

    /**
     * Generates a secure password hash using the configured encoder.
     * <p>Mainly used for testing or administrative purposes.
     *
     * @param password raw plain-text password
     * @return encoded password hash
     */
    String getPasswordHash(String password);
}