package online.walletiq.service;

import online.walletiq.dto.chat.*;
import online.walletiq.dto.chat.*;
import online.walletiq.exception.ChatSessionException;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    /**
     * Retrieves all chat sessions for the current user.
     * <p>Sessions are ordered by most recently updated.
     *
     * @return list of chat session responses
     */
    List<ChatSessionResponse> getSessions();

    /**
     * Creates a new chat session for the current user.
     *
     * <p>If no title is provided, a default title "New Chat" is assigned.
     *
     * @param request session creation request
     * @return created chat session response
     */
    ChatSessionResponse createSession(CreateSessionRequest request);

    /**
     * Deletes a chat session owned by the current user.
     *
     * <p>Associated messages are deleted via cascading.
     *
     * @param sessionId session identifier
     * @throws ChatSessionException if session is not found or not owned by user
     */
    void deleteSession(UUID sessionId);

    /**
     * Retrieves all messages for a given chat session.
     *
     * <p>Messages are ordered chronologically.
     *
     * @param sessionId session identifier
     * @return list of chat message responses
     * @throws ChatSessionException if session is not found or not owned by user
     */
    List<ChatMessageResponse> getMessages(UUID sessionId);

    /**
     * Processes a user query within a chat session.
     *
     * @param sessionId chat session identifier
     * @param request   query request containing user question
     * @return response containing generated answer
     * @throws ChatSessionException if session is not found or not owned by user
     */
    ChatQueryResponse query(UUID sessionId, ChatQueryRequest request);

}
