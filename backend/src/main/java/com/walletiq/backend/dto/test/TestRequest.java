package com.walletiq.backend.dto.test;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TestRequest(

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email address is required")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {
}
