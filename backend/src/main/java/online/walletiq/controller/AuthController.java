package online.walletiq.controller;

import online.walletiq.dto.auth.*;
import online.walletiq.dto.auth.*;
import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.otp.OtpResponse;
import online.walletiq.dto.otp.SendOtpRequest;
import online.walletiq.dto.otp.VerifyOtpRequest;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.service.AuthService;
import online.walletiq.service.EmailVerificationService;
import online.walletiq.util.ResponseUtil;
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
@Tag(
    name = "Authentication",
    description = "Endpoints for user authentication, session management, and email verification."
)
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Operation(
        summary = "Register a new user",
        description = """
            Creates a new user account using the provided email, name, and password.
            
            Behaviour:
            - The email must be unique in the system.
            - Password is securely hashed before storing in the database.
            - No tokens are issued during signup.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<String>> signup(
        @Valid @RequestBody SignupRequest request) {

        authService.signup(request);
        return ResponseUtil.created("User registered successfully", null);
    }

    @Operation(
        summary = "Authenticate user",
        description = """
            Authenticates a user using email and password.
            
            Behaviour:
            - If credentials are valid, a JWT access token and refresh token are issued.
            - The access token is short-lived and used for API authorization.
            - The refresh token is stored in the database and used to obtain new tokens.
            - Any previously active refresh token for the user is revoked (single-session rule).
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid email or password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request) {

        return ResponseUtil.ok(
            "Login successful",
            authService.login(request)
        );
    }

    @Operation(
        summary = "Logout user",
        description = """
            Logs out a user by revoking the provided refresh token.
            
            Behaviour:
            - The refresh token is marked as revoked in the database.
            - Once revoked, the token can no longer be used to refresh sessions.
            - If the token is already revoked, the operation safely returns without error.
            
            **Note** - Token blacklisting is not yet implemented.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Logout successful"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Refresh token not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Valid @RequestBody LogoutRequest request) {

        authService.logout(request.refreshToken());
        return ResponseUtil.noContent();
    }

    @Operation(
        summary = "Refresh authentication tokens",
        description = """
            Generates a new access token using a valid refresh token.
            
            Behaviour:
            - The provided refresh token is validated for signature and expiration.
            - The existing refresh token is revoked (token rotation).
            - A new access token and refresh token pair is issued.
            - This helps prevent replay attacks and token reuse.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Refresh token not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseWrapper<TokenResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseUtil.ok(
            "Token refreshed successfully",
            authService.refreshToken(request.refreshToken())
        );
    }

    @Operation(
        summary = "Generate password hash",
        description = """
            Utility endpoint that generates a secure hash for a given password.
            
            Behaviour:
            - Uses the configured password encoder (typically BCrypt).
            - Mainly intended for testing or administrative usage.
            - The raw password is not stored by the system.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Password hash generated successfully",
            content = @Content(schema = @Schema(implementation = PasswordHashResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid password input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/password-hash")
    public ResponseEntity<ResponseWrapper<PasswordHashResponse>> getPasswordHash(
        @Valid @RequestBody PasswordHashRequest request) {

        String hash = authService.getPasswordHash(request.password());

        var response = new PasswordHashResponse(
            request.password(),
            hash
        );

        return ResponseUtil.ok(
            "Password hash generated successfully",
            response
        );
    }

    @Operation(
        summary = "Send email verification OTP",
        description = """
            Sends a 6-digit OTP (One-Time Password) to the user's registered email address.
            
            Behaviour:
            - A random OTP is generated and delivered via email.
            - The OTP is stored temporarily with an expiration time.
            - The OTP must be verified before enabling email-based features.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OTP sent successfully",
            content = @Content(schema = @Schema(implementation = OtpResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/email/send-otp")
    public ResponseEntity<ResponseWrapper<OtpResponse>> sendOtp(
        @Valid @RequestBody SendOtpRequest request) {

        emailVerificationService.sendOtp(request.email());

        return ResponseUtil.ok(
            "OTP sent successfully",
            new OtpResponse("A 6-digit OTP has been sent to your email address.")
        );
    }

    @Operation(
        summary = "Verify email OTP",
        description = """
            Verifies the OTP sent to the user's email address.
            
            Behaviour:
            - The provided OTP is matched against the stored OTP.
            - If valid and not expired, the user's email is marked as verified.
            - Invalid or expired OTPs will be rejected.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Email verified successfully",
            content = @Content(schema = @Schema(implementation = OtpResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired OTP",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/email/verify-otp")
    public ResponseEntity<ResponseWrapper<OtpResponse>> verifyOtp(
        @Valid @RequestBody VerifyOtpRequest request) {

        emailVerificationService.verifyOtp(
            request.email(),
            request.otp()
        );

        return ResponseUtil.ok(
            "Email verified successfully",
            new OtpResponse("Your email has been verified successfully.")
        );
    }
}