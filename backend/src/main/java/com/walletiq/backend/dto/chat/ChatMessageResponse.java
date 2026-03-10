package com.walletiq.backend.dto.chat;

import com.walletiq.backend.enums.MessageRole;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageResponse(
    UUID id,
    MessageRole role,   // USER | ASSISTANT
    String content,
    Instant createdAt
) {}