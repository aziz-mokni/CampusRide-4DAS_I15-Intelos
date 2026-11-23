package com.campusride.notification.domain.model;

import lombok.*;
import java.time.Instant;
import java.util.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Conversation {
    private String id;
    private String tripId;
    @Builder.Default private List<String> participantIds = new ArrayList<>();
    @Builder.Default private List<Message> messages = new ArrayList<>();

    public boolean isParticipant(String userId) {
        return participantIds.contains(userId);
    }

    public Message addMessage(String senderId, String text) {
        if (!isParticipant(senderId)) {
            throw new IllegalStateException("Sender is not a participant of this conversation");
        }
        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .senderId(senderId)
                .text(text)
                .sentAt(Instant.now())
                .build();
        messages.add(message);
        return message;
    }
}