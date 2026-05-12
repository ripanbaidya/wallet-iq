package online.walletiq.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "chat_sessions",
    indexes = @Index(name = "idx_chat_sessions_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
public class ChatSession extends BaseEntity {

    // Default title is "New Chat", user can rename it
    @Column(name = "title", length = 255, nullable = false)
    private String title = "New Chat";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "session", orphanRemoval = true,
        cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChatMessage> messages = new ArrayList<>();
}