package com.walletiq.service;

import com.walletiq.dto.notification.NotificationResponse;
import com.walletiq.enums.NotificationType;
import com.walletiq.exception.NotificationException;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    /**
     * Creates and sends a notification to the current user.
     * <p>The notification is persisted and immediately published
     * via WebSocket for real-time delivery.
     *
     * @param type    notification type
     * @param message notification message
     */
    void send(NotificationType type, String message);

    /**
     * Retrieves all notifications for the current user.
     * <p>Results are ordered by creation time in descending order.
     *
     * @return list of notification responses
     */
    List<NotificationResponse> getAll();

    /**
     * Deletes a notification by its ID.
     * <p>Ensures that the notification belongs to the current user.
     * If the notification does not exist or belongs to another user,
     * a not found exception is thrown to avoid information leakage.
     *
     * @param notificationId notification identifier
     * @throws NotificationException if notification is not found or access is denied
     */
    void deleteById(UUID notificationId);

    /**
     * Deletes all notifications for the current user.
     */
    void deleteAll();
}