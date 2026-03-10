package com.walletiq.backend.service;

import com.walletiq.backend.dto.chat.*;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ChatSessionResponse> getSessions();

    ChatSessionResponse createSession(CreateSessionRequest request);

    void deleteSession(UUID sessionId);

    List<ChatMessageResponse> getMessages(UUID sessionId);

    ChatQueryResponse query(UUID sessionId, ChatQueryRequest request);

}
