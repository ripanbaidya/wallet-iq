package online.walletiq.dto.chat;

import online.walletiq.enums.MessageRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Represents a chat message in a conversation session")
public record ChatMessageResponse(

    @Schema(
        description = "Unique identifier of the chat message",
        example = "3f9e6b8e-5b8c-4a64-b1e2-7d3a0a6d4f22"
    )
    UUID id,

    @Schema(
        description = "Role of the message sender",
        example = "USER",
        allowableValues = {"USER", "ASSISTANT"}
    )
    MessageRole role,

    @Schema(
        description = "Content of the chat message",
        example = "Show my expenses for this month."
    )
    String content,

    @Schema(
        description = "Timestamp when the message was created",
        example = "2026-03-16T12:30:45Z"
    )
    Instant createdAt

) {
}