package com.walletiq.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.walletiq.enums.MessageRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

// ChatMessage does NOT extend BaseEntity intentionally.
// Messages are immutable once saved — they are never updated.
@Entity
@Table(
    name = "chat_messages",
    indexes = {
        // Composite index — the most common query is:
        // "give me all messages for session X ordered by time"
        @Index(name = "idx_chat_messages_session_created", columnList = "session_id, created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private MessageRole role;           // USER or ASSISTANT

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference
    private ChatSession session;

    // Drives message ordering within a session
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}