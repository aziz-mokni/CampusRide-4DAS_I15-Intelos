package com.campusride.notification.domain.port;

import com.campusride.notification.domain.model.Conversation;
import java.util.Optional;

public interface ConversationRepository {
    Optional<Conversation> findById(String id);
    Conversation save(Conversation conversation);
}