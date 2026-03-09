package com.walletiq.backend.controller;

import com.walletiq.backend.dto.auth.LoginRequest;
import com.walletiq.backend.dto.auth.LogoutRequest;
import com.walletiq.backend.dto.auth.RefreshTokenRequest;
import com.walletiq.backend.dto.auth.SignupRequest;
import com.walletiq.backend.dto.auth.AuthResponse;
import com.walletiq.backend.dto.auth.TokenResponse;
import com.walletiq.backend.dto.success.ResponseWrapper;
import com.walletiq.backend.service.AuthService;
import com.walletiq.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<AuthResponse>> signup(
        @Valid @RequestBody SignupRequest request) {
        return ResponseUtil.created("User registered successfully",
            authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request) {
        return ResponseUtil.ok("Login Successfully", authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseWrapper<TokenResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseUtil.ok("Token Refreshed Successfully",
            authService.refreshToken(request.refreshToken()));
    }
}
