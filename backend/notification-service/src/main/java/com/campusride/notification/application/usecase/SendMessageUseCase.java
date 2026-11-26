package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.exception.ForbiddenOperationException;
import com.campusride.notification.domain.exception.ResourceNotFoundException;
import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.model.Message;
import com.campusride.notification.domain.port.ConversationRepository;

public class SendMessageUseCase {

    private final ConversationRepository conversations;

    public SendMessageUseCase(ConversationRepository conversations) {
        this.conversations = conversations;
    }

    public Message execute(String conversationId, String senderId, String text) {
        Conversation c = conversations.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation " + conversationId + " not found"));
        if (!c.isParticipant(senderId)) {
            throw new ForbiddenOperationException("You are not a participant of this conversation");
        }
        Message message = c.addMessage(senderId, text);
        conversations.save(c);
        return message;
    }
}
