package com.campusride.notification.presentation.dto;

import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.enums.NotificationType;
import com.campusride.notification.domain.model.Notification;
import java.time.Instant;

public record NotificationResponse(String id, String userId, NotificationType type, Channel channel,
                                   NotificationStatus status, String message, Instant createdAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getUserId(), n.getType(), n.getChannel(),
                n.getStatus(), n.getMessage(), n.getCreatedAt());
    }
}
