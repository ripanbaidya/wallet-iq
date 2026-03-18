package com.walletiq.dto.user;

import com.walletiq.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authenticated user details returned after successful login or registration")
public record AuthUserResponse(

    @Schema(
        description = "Unique identifier of the user",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    String id,

    @Schema(
        description = "Full name of the user",
        example = "Ripan Baidya"
    )
    String fullName,

    @Schema(
        description = "Email address of the user",
        example = "ripan@example.com"
    )
    String email

) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
            user.getId().toString(),
            user.getFullName(), user.getEmail());
    }
}