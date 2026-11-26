package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.enums.NotificationType;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.port.NotificationRepository;

import java.time.Instant;

/** Creation manuelle d'une notification IN_APP (administrateur). */
public class CreateNotificationUseCase {

    private final NotificationRepository notifications;

    public CreateNotificationUseCase(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    public Notification execute(String userId, NotificationType type, String message) {
        return notifications.save(Notification.builder()
                .userId(userId).type(type).channel(Channel.IN_APP)
                .status(NotificationStatus.SENT).message(message).createdAt(Instant.now()).build());
    }
}
