package com.walletiq.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for AI-related components.
 * Exposes a {@link ChatClient} bean backed by the Ollama chat model,
 * used for interacting with LLM-powered features within the application.
 */
@Configuration
public class AiConfig {

    /**
     * Creates a {@link ChatClient} using the configured Ollama chat model.
     */
    @Bean
    @Profile("dev")
    public ChatClient ollamChatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * Creates a {@link ChatClient} using the configured OpenAI chat model.
     */
    @Bean
    @Profile("prod")
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}