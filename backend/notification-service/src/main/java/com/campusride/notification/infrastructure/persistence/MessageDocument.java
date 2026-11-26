package com.campusride.notification.infrastructure.persistence;

import lombok.*;
import java.time.Instant;

/** Message integre dans une conversation (pas de cle etrangere). */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MessageDocument {
    private String id;
    private String senderId;
    private String text;
    private Instant sentAt;
}
