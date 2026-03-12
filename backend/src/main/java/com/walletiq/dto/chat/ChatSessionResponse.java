package com.walletiq.dto.chat;

import java.time.Instant;
import java.util.UUID;

public record ChatSessionResponse(

    UUID id,
    String title,
    Instant createdAt,
    Instant updatedAt
) {
}