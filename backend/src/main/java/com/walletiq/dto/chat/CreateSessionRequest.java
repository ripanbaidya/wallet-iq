package com.walletiq.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record CreateSessionRequest(

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(
        description = "Optional title of the chat session. If not provided, the system defaults to 'New Chat'.",
        example = ""
    )
    String title
) {
}