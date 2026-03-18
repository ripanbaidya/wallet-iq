package com.walletiq.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Represents a chat session belonging to a user")
public record ChatSessionResponse(

    @Schema(
        description = "Unique identifier of the chat session",
        example = "c9d1a0f2-7e2c-4b3e-9a6d-3f4e7a8c9b10"
    )
    UUID id,

    @Schema(
        description = "Title of the chat session",
        example = "Monthly Expense Analysis"
    )
    String title,

    @Schema(
        description = "Timestamp when the chat session was created",
        example = "2026-03-16T10:15:30Z"
    )
    Instant createdAt,

    @Schema(
        description = "Timestamp when the chat session was last updated",
        example = "2026-03-16T10:20:45Z"
    )
    Instant updatedAt

) {
}