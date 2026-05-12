package online.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "LoginRequest", description = "User login credentials")
public record LoginRequest(

    @Schema(
        description = "Registered email address of the user",
        example = "ripanbaidya@gmail.com"
    )
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Schema(
        description = "User account password",
        example = "MySecurePassword123!",
        minLength = 8,
        maxLength = 64
    )
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    String password

) {
}