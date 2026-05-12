package online.walletiq.dto.otp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for verifying an email OTP")
public record VerifyOtpRequest(

    @Schema(
        description = "Email address associated with the OTP",
        example = "ripan@gmail.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Schema(
        description = "6-digit OTP sent to the user's email",
        example = "123456",
        minLength = 6,
        maxLength = 6,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "OTP must not be blank")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
    String otp

) {
}