package com.walletiq.dto.user;

import java.util.UUID;

public record UserProfileResponse(

    UUID id,
    String name,
    String email,
    boolean active,
    boolean isEmailVerified
) {
}
