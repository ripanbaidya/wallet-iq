package com.walletiq.dto.chat;

import com.walletiq.enums.MessageRole;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
    UUID id,
    MessageRole role,   // USER | ASSISTANT
    String content,
    Instant createdAt
) {}