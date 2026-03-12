package com.walletiq.service.impl;

import com.walletiq.dto.chat.*;
import com.walletiq.entity.ChatMessage;
import com.walletiq.entity.ChatSession;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.MessageRole;
import com.walletiq.exception.handler.ChatSessionException;
import com.walletiq.mapper.ChatMapper;
import com.walletiq.repository.ChatMessageRepository;
import com.walletiq.repository.ChatSessionRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.ChatService;
import com.walletiq.service.RAGQueryService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final RAGQueryService ragQueryService;

    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getSessions() {
        User user = currentUser();

        return chatSessionRepository.findByUserOrderByUpdatedAtDesc(user)
            .stream()
            .map(ChatMapper::toSessionResponse)
            .toList();
    }

    @Override
    @Transactional
    public ChatSessionResponse createSession(CreateSessionRequest req) {
        User user = currentUser();

        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setTitle(Optional.ofNullable(req.title())
            .filter(s -> !s.isBlank())
            .orElse("New Chat"));

        ChatSession createdSession = chatSessionRepository.saveAndFlush(session);

        return ChatMapper.toSessionResponse(createdSession);
    }

    @Override
    @Transactional
    public void deleteSession(UUID sessionId) {
        ChatSession session = getOwnedSession(sessionId);

        chatSessionRepository.delete(session);   // Cascades to messages
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(UUID sessionId) {
        getOwnedSession(sessionId);

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

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

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
}