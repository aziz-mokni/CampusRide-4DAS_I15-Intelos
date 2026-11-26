package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.exception.ForbiddenOperationException;
import com.campusride.notification.domain.exception.ResourceNotFoundException;
import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.model.Message;
import com.campusride.notification.domain.port.ConversationRepository;

import java.util.List;

public class GetMessagesUseCase {

    private final ConversationRepository conversations;

    public GetMessagesUseCase(ConversationRepository conversations) {
        this.conversations = conversations;
    }

    public List<Message> execute(String conversationId, String userId) {
        Conversation c = conversations.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation " + conversationId + " not found"));
        if (!c.isParticipant(userId)) {
            throw new ForbiddenOperationException("You are not a participant of this conversation");
        }
        return c.getMessages();
    }
}
