package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.port.NotificationRepository;

import java.util.List;

public class ListNotificationsUseCase {

    private final NotificationRepository notifications;

    public ListNotificationsUseCase(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    public List<Notification> execute(String userId, NotificationStatus status, Channel channel, int page, int size) {
        return notifications.findByUserId(userId, status, channel, page, size);
    }
}
