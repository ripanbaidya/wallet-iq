package com.walletiq.backend.dto.chat;

import jakarta.validation.constraints.Size;

public record CreateSessionRequest(
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title   // optional — defaults to "New Chat" in service
) {
}