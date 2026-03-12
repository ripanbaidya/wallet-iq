package com.walletiq.backend.controller;

import com.walletiq.backend.dto.auth.*;
import com.walletiq.backend.dto.otp.OtpResponse;
import com.walletiq.backend.dto.otp.SendOtpRequest;
import com.walletiq.backend.dto.otp.VerifyOtpRequest;
import com.walletiq.backend.dto.success.ResponseWrapper;
import com.walletiq.backend.service.AuthService;
import com.walletiq.backend.service.EmailVerificationService;
import com.walletiq.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final EmailVerificationService emailVerificationService;

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

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseWrapper<TokenResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseUtil.ok("Token Refreshed Successfully",
            authService.refreshToken(request.refreshToken()));
    }

    @Operation(
        summary = "Generate password hash",
        description = "Hashes a plain text password using the configured password encoder (e.g., BCrypt)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password successfully hashed"),
        @ApiResponse(responseCode = "400", description = "Invalid password input")
    })
    @PostMapping("/password-hash")
    public ResponseEntity<ResponseWrapper<PasswordHashResponse>> getPasswordHash(
        @Valid @RequestBody PasswordHashRequest request) {
        String hash = authService.getPasswordHash(request.password());
        var response = new PasswordHashResponse(request.password(), hash);

        return ResponseUtil.ok("Password Hash Generated Successfully",
            response);
    }

    @PostMapping("/email/send-otp")
    @Operation(
        summary = "Send OTP to email",
        description = "Generates a 6-digit OTP and sends it to the user's registered email address."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "OTP sent successfully",
            content = @Content(schema = @Schema(implementation = OtpResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ResponseWrapper<OtpResponse>> sendOtp(
        @RequestBody @Valid SendOtpRequest request
    ) {
        emailVerificationService.sendOtp(request.email());
        return ResponseUtil.ok("OTP sent successfully", new OtpResponse(
            "A 6-digit OTP has been sent to your email address."
        ));
    }

    @Operation(
        summary = "Verify OTP",
        description = "Verifies the OTP sent to the user's email address."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email verified successfully",
            content = @Content(schema = @Schema(implementation = OtpResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/email/verify-otp")
    public ResponseEntity<ResponseWrapper<OtpResponse>> verifyOtp(
        @RequestBody @Valid VerifyOtpRequest request
    ) {
        emailVerificationService.verifyOtp(request.email(), request.otp());
        return ResponseUtil.ok("Email verified", new OtpResponse(
            "Your email has been verified successfully."
        ));
    }

}
