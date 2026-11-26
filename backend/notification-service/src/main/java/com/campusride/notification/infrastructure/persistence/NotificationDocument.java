package com.campusride.notification.infrastructure.persistence;

import com.campusride.notification.domain.enums.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "notifications")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationDocument {
    @Id private String id;
    @Indexed private String userId;
    private NotificationType type;
    private Channel channel;
    private NotificationStatus status;
    private String message;
    private Instant createdAt;
}
