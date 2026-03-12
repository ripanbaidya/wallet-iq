package com.walletiq.backend.service;

import com.walletiq.backend.dto.auth.LoginRequest;
import com.walletiq.backend.dto.auth.SignupRequest;
import com.walletiq.backend.dto.auth.AuthResponse;
import com.walletiq.backend.dto.auth.TokenResponse;

public interface AuthService {

   AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    String getPasswordHash(String password);
}