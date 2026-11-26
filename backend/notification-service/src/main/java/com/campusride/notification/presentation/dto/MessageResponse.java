package com.campusride.notification.presentation.dto;

import com.campusride.notification.domain.model.Message;
import java.time.Instant;

public record MessageResponse(String id, String senderId, String text, Instant sentAt) {

    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getSenderId(), m.getText(), m.getSentAt());
    }
}
