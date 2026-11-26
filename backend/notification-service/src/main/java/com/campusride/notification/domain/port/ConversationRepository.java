package com.campusride.notification.domain.port;

import com.campusride.notification.domain.model.Conversation;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository {
    Optional<Conversation> findById(String id);
    List<Conversation> findByParticipantId(String userId);
    Conversation save(Conversation conversation);
}
