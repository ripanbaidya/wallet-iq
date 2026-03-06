package com.walletiq.backend.service;

import com.walletiq.backend.payload.request.LoginRequest;
import com.walletiq.backend.payload.request.LogoutRequest;
import com.walletiq.backend.payload.request.SignupRequest;
import com.walletiq.backend.payload.response.AuthResponse;
import com.walletiq.backend.payload.response.TokenResponse;

public interface AuthService {

   AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}