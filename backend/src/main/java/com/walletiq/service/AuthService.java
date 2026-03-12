package com.walletiq.service;

import com.walletiq.dto.auth.LoginRequest;
import com.walletiq.dto.auth.SignupRequest;
import com.walletiq.dto.auth.AuthResponse;
import com.walletiq.dto.auth.TokenResponse;

public interface AuthService {

    void signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    String getPasswordHash(String password);
}