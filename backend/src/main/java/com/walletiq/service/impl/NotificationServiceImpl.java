package com.walletiq.service.impl;

import com.walletiq.dto.notification.NotificationResponse;
import com.walletiq.entity.Notification;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.NotificationType;
import com.walletiq.exception.NotificationException;
import com.walletiq.repository.NotificationRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.NotificationService;
import com.walletiq.util.SecurityUtils;
import com.walletiq.websocket.WebSocketNotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link NotificationService}.
 * <p>Handles notification lifecycle including creation, retrieval, deletion,
 * and real-time delivery via WebSocket.
 * <p>Ensures that notifications are scoped to the currently authenticated user
 * and prevents data leakage across users.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final WebSocketNotificationPublisher publisher;

    @Override
    @Transactional
    public void send(NotificationType type, String message) {
        UUID userId = currentUserId();

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);

        var saved = notificationRepository.save(notification);
        publisher.publish(userId, toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll() {
        return notificationRepository
            .findByUserIdOrderByCreatedAtDesc(currentUserId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID notificationId) {
        UUID userId = currentUserId();

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // Don't leak existence — treat wrong owner same as not found
        if (!notification.getUserId().equals(userId)) {
            throw new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void deleteAll() {
        notificationRepository.deleteAllByUserId(currentUserId());
    }

    // Helper methods

    private UUID currentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    // Mapper

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
            .id(n.getId())
            .type(n.getType())
            .message(n.getMessage())
            .createdAt(n.getCreatedAt())
            .build();
    }

}
