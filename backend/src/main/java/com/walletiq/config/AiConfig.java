package com.walletiq.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;

/**
 * Configuration for AI-related components.
 * Exposes a {@link ChatClient} bean backed by the Ollama chat model,
 * used for interacting with LLM-powered features within the application.
 */
@Configuration
public class AiConfig {

    /**
     * Creates a {@link ChatClient} using the configured Ollama chat model.
     * Note: Change the model as per the requirements.
     */
    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}