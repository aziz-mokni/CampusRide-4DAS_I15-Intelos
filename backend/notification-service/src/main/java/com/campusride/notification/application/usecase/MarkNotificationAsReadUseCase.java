package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.exception.ForbiddenOperationException;
import com.campusride.notification.domain.exception.ResourceNotFoundException;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.port.NotificationRepository;

public class MarkNotificationAsReadUseCase {

    private final NotificationRepository notifications;

    public MarkNotificationAsReadUseCase(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    public Notification execute(String notificationId, String userId) {
        Notification n = notifications.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification " + notificationId + " not found"));
        if (!n.belongsTo(userId)) {
            throw new ForbiddenOperationException("This notification belongs to another user");
        }
        n.markAsRead();
        return notifications.save(n);
    }
}
