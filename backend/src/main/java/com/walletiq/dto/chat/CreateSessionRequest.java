package com.walletiq.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new chat session")
public record CreateSessionRequest(

    @Schema(
        description = "Optional title of the chat session. If omitted, the system defaults to 'New Chat'.",
        example = "New Chat",
        maxLength = 255
    )
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title

) {
}