package com.walletiq.backend.service.impl;

import com.walletiq.backend.dto.chat.*;
import com.walletiq.backend.entity.ChatMessage;
import com.walletiq.backend.entity.ChatSession;
import com.walletiq.backend.entity.User;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.enums.MessageRole;
import com.walletiq.backend.exception.handler.ChatSessionException;
import com.walletiq.backend.mapper.ChatMapper;
import com.walletiq.backend.repository.ChatMessageRepository;
import com.walletiq.backend.repository.ChatSessionRepository;
import com.walletiq.backend.repository.UserRepository;
import com.walletiq.backend.service.ChatService;
import com.walletiq.backend.service.RAGQueryService;
import com.walletiq.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RAGQueryService ragQueryService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getSessions() {
        User user = currentUser();
        return chatSessionRepository.findByUserOrderByUpdatedAtDesc(user)
            .stream().map(ChatMapper::toSessionResponse).toList();
    }

    @Override
    public ChatSessionResponse createSession(CreateSessionRequest req) {
        User user = currentUser();

        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setTitle(req.title() != null ? req.title() : "New Chat");

        var createdSession = chatSessionRepository.save(session);
        log.debug("Chat session created: {}", createdSession);

        return ChatMapper.toSessionResponse(createdSession);
    }

    @Override
    public void deleteSession(UUID sessionId) {
        ChatSession session = getOwnedSession(sessionId);
        chatSessionRepository.delete(session);   // cascades to messages
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(UUID sessionId) {
        getOwnedSession(sessionId);    // ownership check
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
            .stream()
            .map(ChatMapper::toMessageResponse)
            .toList();
    }

    @Override
    public ChatQueryResponse query(UUID sessionId, ChatQueryRequest req) {
        ChatSession session = getOwnedSession(sessionId);
        User user = currentUser();

        // Persist user message
        saveMessage(session, MessageRole.USER, req.question());

        // RAG
        String answer = ragQueryService.query(req.question(), user.getId());

        // Persist assistant reply
        saveMessage(session, MessageRole.ASSISTANT, answer);

        // Bump session updatedAt
        session.setTitle(deriveTitle(session, req.question()));
        chatSessionRepository.save(session);

        return new ChatQueryResponse(answer);
    }

    // Helper

    private ChatSession getOwnedSession(UUID sessionId) {
        User user = currentUser();
        return chatSessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new ChatSessionException(ErrorCode.CHAT_SESSION_NOT_FOUND));
    }

    private void saveMessage(ChatSession session, MessageRole role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setSession(session);
        msg.setRole(role);
        msg.setContent(content);
        chatMessageRepository.save(msg);
    }

    /**
     * Use the first question as the session title (only set once)
     */
    private String deriveTitle(ChatSession session, String question) {
        if (!"New Chat".equals(session.getTitle())) return session.getTitle();
        return question.length() > 60 ? question.substring(0, 57) + "..." : question;
    }

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }
}