package com.walletiq.service;

import com.walletiq.dto.chat.*;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ChatSessionResponse> getSessions();

    ChatSessionResponse createSession(CreateSessionRequest request);

    void deleteSession(UUID sessionId);

    List<ChatMessageResponse> getMessages(UUID sessionId);

    ChatQueryResponse query(UUID sessionId, ChatQueryRequest request);

}
