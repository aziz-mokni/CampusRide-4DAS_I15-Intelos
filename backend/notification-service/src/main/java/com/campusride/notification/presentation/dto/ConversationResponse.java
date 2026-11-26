package com.campusride.notification.presentation.dto;

import com.campusride.notification.domain.model.Conversation;
import java.util.List;

public record ConversationResponse(String id, String tripId, List<String> participantIds, int messageCount) {

    public static ConversationResponse from(Conversation c) {
        return new ConversationResponse(c.getId(), c.getTripId(), c.getParticipantIds(), c.getMessages().size());
    }
}
