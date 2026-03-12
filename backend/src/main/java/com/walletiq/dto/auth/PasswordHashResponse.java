package com.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the hashed password")
public record PasswordHashResponse(

    @Schema(
        description = "Original password that was hashed",
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