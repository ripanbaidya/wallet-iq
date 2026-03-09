package com.walletiq.backend.dto.user;

import com.walletiq.backend.entity.User;

/**
 * Response DTO containing authenticated user information.
 * Returned by login and registration endpoints.
 */
public record AuthUserResponse(
    String id,
    String fullName,
    String email
) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(user.getId().toString(), user.getFullName(),
            user.getEmail());
    }
}