package com.campusride.notification.domain.model;

import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Message {
    private String id;
    private String senderId;
    private String text;
    private Instant sentAt;
}