package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.port.ConversationRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CreateConversationUseCase {

    private final ConversationRepository conversations;

    public CreateConversationUseCase(ConversationRepository conversations) {
        this.conversations = conversations;
    }

    /** Le createur est automatiquement participant. */
    public Conversation execute(String creatorId, String tripId, List<String> participantIds) {
        Set<String> participants = new LinkedHashSet<>();
        participants.add(creatorId);
        if (participantIds != null) participants.addAll(participantIds);
        return conversations.save(Conversation.builder()
                .tripId(tripId)
                .participantIds(new ArrayList<>(participants))
                .messages(new ArrayList<>())
                .build());
    }
}
