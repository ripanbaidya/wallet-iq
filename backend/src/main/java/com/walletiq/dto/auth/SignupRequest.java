package com.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for user registration")
public record SignupRequest(

    @Schema(
        description = "Full name of the user",
        example = "Ripan Baidya",
        minLength = 2,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[A-Za-z .'-]+$",
        message = "Full name can only contain letters, spaces, and valid symbols (.-')"
    )
    String fullName,

    @Schema(
        description = "User email address used for authentication",
        example = "ripan@gmail.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Schema(
        description = "User account password",
        example = "MySecure123",
        minLength = 8,
        maxLength = 64,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$",
        message = "Password must contain at least one letter and one number"
    )
    String password

) {
}