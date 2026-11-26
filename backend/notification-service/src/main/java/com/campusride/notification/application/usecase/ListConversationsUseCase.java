package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.port.ConversationRepository;

import java.util.List;

public class ListConversationsUseCase {

    private final ConversationRepository conversations;

    public ListConversationsUseCase(ConversationRepository conversations) {
        this.conversations = conversations;
    }

    public List<Conversation> execute(String userId) {
        return conversations.findByParticipantId(userId);
    }
}
