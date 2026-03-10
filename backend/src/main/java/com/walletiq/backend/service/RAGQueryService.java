package com.walletiq.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RAGQueryService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    private static final int TOP_K = 8;

    private static final String SYSTEM_PROMPT = """
        You are WalletIQ, a personal finance assistant.
        Answer the user's question using ONLY the transaction data provided in the context.
        If the context doesn't contain enough information, say so honestly.
        Be concise, specific, and use numbers where relevant.
        Do not make up transactions or amounts.
        """;

    public String query(String question, UUID userId) {
        // Step 1 — retrieve relevant transactions for this user only
        List<Document> context = retrieve(question, userId);

        if (context.isEmpty()) {
            return "I couldn't find any relevant transactions to answer that question.";
        }

        // Step 2 — format context block
        String contextBlock = context.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n---\n"));

        // Step 3 — call LLM
        return chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user("""
                Context (your transactions):
                %s

                Question: %s
                """.formatted(contextBlock, question))
            .call()
            .content();
    }

    private List<Document> retrieve(String question, UUID userId) {
        // Filter ensures a user NEVER gets another user's transactions
        SearchRequest request = SearchRequest.builder()
            .query(question)
            .topK(TOP_K)
            .filterExpression("userId == '" + userId + "'")
            .build();

        try {
            return vectorStore.similaritySearch(request);
        } catch (Exception e) {
            log.error("Vector search failed for userId={}", userId, e);
            return List.of();
        }
    }
}
