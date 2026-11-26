package com.campusride.notification.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "processed_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProcessedEventDocument {
    @Id private String eventId;
    private Instant processedAt;
}
