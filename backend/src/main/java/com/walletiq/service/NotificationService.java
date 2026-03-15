package com.walletiq.service;

import com.walletiq.dto.notification.NotificationResponse;
import com.walletiq.enums.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void send(NotificationType type, String message);

    List<NotificationResponse> getAll();

    void deleteById(UUID notificationId);

    void deleteAll();
}