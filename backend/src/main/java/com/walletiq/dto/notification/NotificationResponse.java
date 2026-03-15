package com.walletiq.dto.notification;

import com.walletiq.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(

    UUID id,
    NotificationType type,
    String message,
    LocalDateTime createdAt
) {
}