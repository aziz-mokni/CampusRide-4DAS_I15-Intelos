package com.campusride.notification.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface SpringDataConversationRepository extends MongoRepository<ConversationDocument, String> {

    @Query("{ 'participantIds' : ?0 }")
    List<ConversationDocument> findByParticipant(String userId);
}
