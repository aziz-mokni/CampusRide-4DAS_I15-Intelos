package com.campusride.notification.domain.port;

import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.model.Notification;
import java.util.*;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(String id);
    List<Notification> findByUserId(String userId, NotificationStatus status, int page, int size);
}