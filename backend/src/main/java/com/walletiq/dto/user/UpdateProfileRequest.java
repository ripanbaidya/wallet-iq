package com.walletiq.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(

    @Schema(description = "User's full name", example = "John Doe")
    @NotBlank(message = "Full name cannot be blank")
    String fullName

) {
}
