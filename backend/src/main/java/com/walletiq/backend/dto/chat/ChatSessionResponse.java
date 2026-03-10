package com.walletiq.backend.dto.chat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSessionResponse(
    UUID id,
    String title,
    Instant createdAt,
    Instant updatedAt
) {}