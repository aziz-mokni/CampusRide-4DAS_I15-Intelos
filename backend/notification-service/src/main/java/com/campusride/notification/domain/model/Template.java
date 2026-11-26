package com.campusride.notification.domain.model;

import lombok.*;
import java.util.Map;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Template {
    private String code;      // ex. "BOOKING_CONFIRMED"
    private String subject;
    private String body;      // ex. "Votre reservation {{bookingId}} est confirmee."

    public String renderSubject(Map<String, String> variables) {
        return render(subject, variables);
    }

    public String renderBody(Map<String, String> variables) {
        return render(body, variables);
    }

    private static String render(String text, Map<String, String> variables) {
        if (text == null) return "";
        String result = text;
        if (variables != null) {
            for (var e : variables.entrySet()) {
                result = result.replace("{{" + e.getKey() + "}}", e.getValue());
            }
        }
        // supprime les variables non renseignees
        return result.replaceAll("\\{\\{[^}]*\\}\\}", "");
    }
}
