package com.walletiq.service;

import com.walletiq.entity.ChatMessage;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.MessageRole;
import com.walletiq.exception.RagQueryException;
import com.walletiq.exception.VectorSearchException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RAGQueryService {

    // The prompt we are using for our AI is defined under the
    private static final String PROMPT_PATH = "prompts/walletiq-system-prompt.txt";

    // Number of most relevant documents retrieved from the vector store.
    // A value between 5–10 generally works well for RAG context windows.
    private static final int TOP_K = 8;
    // Cap to last 10 messages to avoid token overflow
    private static final int MAX_HISTORY = 10;


    // Vector store used for semantic search over embedded transaction documents.
    private final VectorStore vectorStore;

    // Spring AI chat client used to interact with the LLM.
    private final ChatClient chatClient;

    private final FinancialContextService financialContextService;

    // Loaded system prompt used for all RAG queries
    private String systemPrompt;

    @PostConstruct
    private void loadPrompt() {
        try {
            ClassPathResource resource = new ClassPathResource(PROMPT_PATH);

            try (var inputStream = resource.getInputStream()) {
                this.systemPrompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            if (systemPrompt.isBlank()) {
                throw new IllegalStateException("System prompt file is empty: " + PROMPT_PATH);
            }

            log.info("WalletIQ system prompt loaded ({} chars) from {}", systemPrompt.length(), PROMPT_PATH);
        } catch (IOException e) {
            throw new IllegalStateException(ErrorCode.RAG_PROMPT_LOAD_FAILED.getDefaultMessage(), e);
        }
    }

    /**
     * Main entry point for RAG <br>
     * Flow: <p>
     * 1. Semantic search → retrieve top-K transactions for this user <br>
     * 2. Build context block for the LLM <br>
     * 3. Call LLM with system prompt + context + question <br>
     * 4. Sanitize the response before returning it to the user
     *
     * @param question The user’s natural language query
     * @param userId   The ID of the user whose transactions should be queried
     * @return Clean formatted LLM response
     * @throws RagQueryException     if the LLM call fails
     * @throws VectorSearchException if the vector store search fails unrecoverably
     */
    public String query(String question, UUID userId, List<ChatMessage> history) {
        // Retrieve relevant transactions for this user only
        List<Document> context = retrieve(question, userId);

        // Add context for Budget & goals
        String budgetContext = financialContextService.buildBudgetContext(userId);
        String goalContext = financialContextService.buildGoalContext(userId);

        // Build combined context block
        String transactionContext = context.isEmpty()
            ? "I couldn't find any relevant transactions to answer that question."
            : context.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n---\n"));

        String fullContext = """
            %s
            
            %s
            
            === RELEVANT TRANSACTIONS ===
            %s
            """.formatted(budgetContext, goalContext, transactionContext);

        // Call LLM
        String rawResponse = callLlm(fullContext, question, userId, history);

        // Sanitize the response
        return sanitizeMarkdown(rawResponse);
    }

    /**
     * Retrieves the most relevant transaction documents from the vector store
     * using semantic similarity search.
     */
    private List<Document> retrieve(String question, UUID userId) {
        SearchRequest request = SearchRequest.builder()
            .query(question)
            .topK(TOP_K)
            .filterExpression("userId == '" + userId.toString() + "'")
            .build();

        try {
            return vectorStore.similaritySearch(request);
        } catch (Exception e) {
            log.error("Vector search failed for userId={}", userId, e);
            throw new VectorSearchException(ErrorCode.VECTOR_SEARCH_FAILED,
                "Vector search failed for userId=" + userId, e
            );
        }
    }

    private String callLlm(String contextBlock, String question, UUID userId,
                           List<ChatMessage> history) {
        try {
            List<ChatMessage> recentHistory = history.size() > MAX_HISTORY
                ? history.subList(history.size() - MAX_HISTORY, history.size())
                : history;

            // Build the conversation history block for context
            String historyBlock = buildHistoryBlock(recentHistory);

            return chatClient.prompt()
                .system(systemPrompt)
                .user("""
                    %s
                    Context (your transactions):
                    %s
                    
                    Question: %s
                    """.formatted(historyBlock, contextBlock, question))
                .call()
                .content();
        } catch (Exception e) {
            log.error("LLM call failed for userId: {} question: {}", userId, question, e);
            throw new RagQueryException(ErrorCode.RAG_QUERY_FAILED, "LLM call failed for userId=" + userId, e);
        }
    }

    /**
     * Formats past conversation messages into a readable block that the LLM can
     * use as context for the current question.
     *
     * @param history the list of previous messages to format
     * @return formatted history block, or empty string if there is no history
     */
    private String buildHistoryBlock(List<ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== CONVERSATION HISTORY ===\n");

        for (ChatMessage message : history) {
            String role = message.getRole() == MessageRole.USER
                ? MessageRole.USER.name() : MessageRole.ASSISTANT.name();
            sb.append(role).append(": ").append(message.getContent()).append("\n");
        }
        sb.append("=== END OF HISTORY ===\n\n");
        return sb.toString();
    }

    /**
     * Cleans up common formatting issues produced by LLM responses.
     * This ensures the response renders cleanly in UI clients.
     */
    private String sanitizeMarkdown(String raw) {
        if (raw == null || raw.isBlank()) {
            return "I don't have enough data to answer that fully 🔍";
        }

        return raw
            // Fix literal escape sequences from some LLMs
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\r", "")

            // Strip code block wrappers if LLM wraps anyway
            .replaceAll("(?s)^```(?:markdown|text)?\\s*", "")
            .replaceAll("(?s)```\\s*$", "")

            // Strip preamble the LLM sneaks in despite instructions
            .replaceAll("(?i)^(based on (the |your )?(data|context|transactions)[,.]?\\s*)", "")
            .replaceAll("(?i)^(certainly[!,.]?\\s*|sure[!,.]?\\s*|of course[!,.]?\\s*)", "")
            .replaceAll("(?i)^(here (is|are) (your|the|a)[^\n]*\n)", "")

            // Normalize line endings and excess whitespace
            .replaceAll("\r\n|\r", "\n")
            .replaceAll("[ \t]+\n", "\n")
            .replaceAll("\n{3,}", "\n\n")

            .trim();
    }
}