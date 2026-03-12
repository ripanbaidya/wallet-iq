package com.walletiq.mapper;

import com.walletiq.dto.chat.ChatMessageResponse;
import com.walletiq.dto.chat.ChatSessionResponse;
import com.walletiq.entity.ChatMessage;
import com.walletiq.entity.ChatSession;

public final class ChatMapper {

    private ChatMapper() {}

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