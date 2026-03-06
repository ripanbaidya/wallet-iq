package com.walletiq.backend.payload.response;

import java.util.UUID;

public record UserProfileResponse(
    UUID id,
    String name,
    String email
) {
}
