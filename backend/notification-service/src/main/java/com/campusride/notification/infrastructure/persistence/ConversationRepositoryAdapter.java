package com.campusride.notification.infrastructure.persistence;

import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.model.Message;
import com.campusride.notification.domain.port.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConversationRepositoryAdapter implements ConversationRepository {

    private final SpringDataConversationRepository repo;

    @Override
    public Optional<Conversation> findById(String id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Conversation> findByParticipantId(String userId) {
        return repo.findByParticipant(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Conversation save(Conversation c) {
        ConversationDocument saved = repo.save(toDocument(c));
        return toDomain(saved);
    }

    private ConversationDocument toDocument(Conversation c) {
        List<MessageDocument> messages = c.getMessages().stream()
                .map(m -> MessageDocument.builder()
                        .id(m.getId()).senderId(m.getSenderId()).text(m.getText()).sentAt(m.getSentAt()).build())
                .toList();
        return ConversationDocument.builder()
                .id(c.getId()).tripId(c.getTripId())
                .participantIds(new ArrayList<>(c.getParticipantIds()))
                .messages(new ArrayList<>(messages))
                .build();
    }

    private Conversation toDomain(ConversationDocument d) {
        List<Message> messages = d.getMessages().stream()
                .map(m -> Message.builder()
                        .id(m.getId()).senderId(m.getSenderId()).text(m.getText()).sentAt(m.getSentAt()).build())
                .toList();
        return Conversation.builder()
                .id(d.getId()).tripId(d.getTripId())
                .participantIds(new ArrayList<>(d.getParticipantIds()))
                .messages(new ArrayList<>(messages))
                .build();
    }
}
