package com.campusride.notification.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "conversations")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ConversationDocument {
    @Id private String id;
    @Indexed private String tripId;
    @Builder.Default private List<String> participantIds = new ArrayList<>();
    @Builder.Default private List<MessageDocument> messages = new ArrayList<>();
}
