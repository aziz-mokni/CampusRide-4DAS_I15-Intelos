package com.campusride.notification.domain.model;

import com.campusride.notification.domain.enums.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {
    private String id;
    private String userId;
    private NotificationType type;
    private Channel channel;
    private NotificationStatus status;
    private String message;
    private Instant createdAt;

    public boolean belongsTo(String userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public void markAsSent()   { this.status = NotificationStatus.SENT; }
    public void markAsFailed() { this.status = NotificationStatus.FAILED; }
    public void markAsRead()   { this.status = NotificationStatus.READ; }
}