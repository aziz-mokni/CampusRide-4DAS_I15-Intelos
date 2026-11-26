package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.exception.ForbiddenOperationException;
import com.campusride.notification.domain.exception.ResourceNotFoundException;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.port.NotificationRepository;

public class GetNotificationUseCase {

    private final NotificationRepository notifications;

    public GetNotificationUseCase(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    public Notification execute(String notificationId, String userId) {
        Notification n = notifications.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification " + notificationId + " not found"));
        if (!n.belongsTo(userId)) {
            throw new ForbiddenOperationException("This notification belongs to another user");
        }
        return n;
    }
}
