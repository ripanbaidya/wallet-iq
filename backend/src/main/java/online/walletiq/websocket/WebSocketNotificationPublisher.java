package online.walletiq.websocket;

import online.walletiq.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketNotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Pushes a real-time notification to the user's personal topic.
     * Topic: /topic/notifications/{userId}
     */
    public void publish(UUID userId, NotificationResponse notification) {
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + userId, notification
        );
    }
}