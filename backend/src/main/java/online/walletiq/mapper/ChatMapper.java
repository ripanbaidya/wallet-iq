package online.walletiq.mapper;

import online.walletiq.dto.chat.ChatMessageResponse;
import online.walletiq.dto.chat.ChatSessionResponse;
import online.walletiq.entity.ChatMessage;
import online.walletiq.entity.ChatSession;

public final class ChatMapper {

    private ChatMapper() {
    }

    public static ChatSessionResponse toSessionResponse(ChatSession session) {
        return new ChatSessionResponse(
            session.getId(),
            session.getTitle(),
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }

    public static ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getRole(),
            message.getContent(),
            message.getCreatedAt()
        );
    }
}