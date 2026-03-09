package com.walletiq.backend.dto.user;

import java.util.UUID;

public record UserProfileResponse(
    UUID id,
    String name,
    String email
) {
}
