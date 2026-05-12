package online.walletiq.service.impl;

import online.walletiq.dto.chat.*;
import online.walletiq.dto.chat.*;
import online.walletiq.entity.ChatMessage;
import online.walletiq.entity.ChatSession;
import online.walletiq.entity.User;
import online.walletiq.enums.ErrorCode;
import online.walletiq.enums.MessageRole;
import online.walletiq.exception.ChatSessionException;
import online.walletiq.exception.SubscriptionException;
import online.walletiq.mapper.ChatMapper;
import online.walletiq.repository.ChatMessageRepository;
import online.walletiq.repository.ChatSessionRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.ChatService;
import online.walletiq.service.RAGQueryService;
import online.walletiq.service.SubscriptionService;
import online.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link ChatService}.
 * <p>Manages chat sessions and messages for the current user, including session
 * lifecycle, message persistence, and query handling.
 * <p>Integrates with {@link RAGQueryService} to generate responses based on user
 * queries.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RAGQueryService ragQueryService;
    private final SubscriptionService subscriptionService;

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
        session.setTitle(normalizeTitle(req.title()));

        ChatSession createdSession = chatSessionRepository.saveAndFlush(session);

        return ChatMapper.toSessionResponse(createdSession);
    }

    @Override
    @Transactional
    public void deleteSession(UUID sessionId) {
        ChatSession session = getOwnedSession(sessionId);
        chatSessionRepository.delete(session); // Cascades to messages
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
    @Transactional
    public ChatQueryResponse query(UUID sessionId, ChatQueryRequest req) {
        User user = currentUser();

        // Checking whether a user has any active subscription or not.
        if (!subscriptionService.hasActiveSubscription(user.getId())) {
            throw new SubscriptionException(ErrorCode.SUBSCRIPTION_REQUIRED);
        }

        ChatSession session = getOwnedSession(sessionId);

        // Fetch existing conversation history BEFORE saving the new user message
        // so the current question is not included in the history passed to the LLM
        List<ChatMessage> history = chatMessageRepository
            .findBySessionIdOrderByCreatedAtAsc(session.getId());

        // Persist user message
        saveMessage(session, MessageRole.USER, req.question());

        // RAG query
        String answer = ragQueryService.query(req.question(), user.getId(), history);

        // Persist assistant reply
        saveMessage(session, MessageRole.ASSISTANT, answer);

        // Update session metadata
        session.setTitle(deriveTitle(session, req.question()));
        chatSessionRepository.save(session);

        return new ChatQueryResponse(answer);
    }

    // ===================== Helpers =====================

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    private ChatSession getOwnedSession(UUID sessionId) {
        User user = currentUser();
        return chatSessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() ->
                new ChatSessionException(ErrorCode.CHAT_SESSION_NOT_FOUND));
    }

    private void saveMessage(ChatSession session, MessageRole role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setSession(session);
        msg.setRole(role);
        msg.setContent(content);
        chatMessageRepository.save(msg);
    }

    /**
     * Normalizes session title input.
     * Handles Swagger default "string", blank values, and nulls.
     */
    private String normalizeTitle(String title) {
        if (title == null) return "New Chat";

        String trimmed = title.trim();
        if (trimmed.isEmpty() || "string".equalsIgnoreCase(trimmed)) {
            return "New Chat";
        }
        return trimmed;
    }

    /**
     * Uses the first user question as session title (only if still default).
     */
    private String deriveTitle(ChatSession session, String question) {
        if (!"New Chat".equals(session.getTitle())) return session.getTitle();

        return question.length() > 60
            ? question.substring(0, 57) + "..."
            : question;
    }
}