package com.campusride.notification.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "templates")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TemplateDocument {
    @Id private String code;
    private String subject;
    private String body;
}
