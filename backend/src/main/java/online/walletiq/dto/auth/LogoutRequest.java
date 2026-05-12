package online.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LogoutRequest", description = "Request to revoke a refresh token during user logout")
public record LogoutRequest(

    @Schema(description = "Refresh token issued during authentication")
    @NotBlank(message = "Refresh token must not be blank")
    String refreshToken

) {
}