package com.campusride.notification.domain.model;

import lombok.*;
import java.util.Map;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Template {
    private String code;      // ex. "BOOKING_CONFIRMED"
    private String subject;
    private String body;      // ex. "Bonjour {{firstName}}, votre réservation est confirmée."

    public String renderBody(Map<String, String> variables) {
        String result = body;
        for (var e : variables.entrySet()) {
            result = result.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        return result;
    }
}