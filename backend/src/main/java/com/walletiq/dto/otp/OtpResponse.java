package com.walletiq.dto.otp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after an OTP operation")
public record OtpResponse(

    @Schema(
        description = "Human-readable message describing the result of the OTP operation",
        example = "OTP has been sent successfully to your email"
    )
    String message

) {
}