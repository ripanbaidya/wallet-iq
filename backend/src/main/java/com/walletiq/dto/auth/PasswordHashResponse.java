package com.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload containing the generated hash for a given password.
 * ⚠ SECURITY NOTE: Returning the original password is generally NOT recommended
 * in production APIs.
 * This DTO should only be used for internal tools, debugging, or testing hashing
 * configuration.
 */
@Schema(description = "Response containing the generated hash of a provided password")
public record PasswordHashResponse(

    @Schema(
        description = "Original plain text password provided in the request",
        example = "MySecurePassword123!"
    )
    String original,

    @Schema(
        description = "BCrypt hashed representation of the password",
        example = "$2a$10$3QYF9j8oBzP4fVvFQyYk.eUo3X4V8tS9m4tZ6s9g4V8y3z8c2KpQK"
    )
    String hash

) {
}