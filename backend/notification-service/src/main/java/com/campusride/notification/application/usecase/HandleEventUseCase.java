package com.campusride.notification.application.usecase;

import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.enums.NotificationType;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.model.Template;
import com.campusride.notification.domain.model.UserContact;
import com.campusride.notification.domain.port.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

/**
 * Traite un evenement RabbitMQ : cree une notification IN_APP pour chaque destinataire,
 * puis tente un e-mail (coordonnees obtenues aupres du service Utilisateurs).
 * Idempotent grace a l'eventId.
 */
@Slf4j
public class HandleEventUseCase {

    private static final List<String> SINGLE_KEYS = List.of("userId", "passengerId", "targetId");
    private static final List<String> LIST_KEYS = List.of("passengerIds", "recipientIds", "userIds");

    private final ProcessedEventRepository processedEvents;
    private final TemplateRepository templates;
    private final NotificationRepository notifications;
    private final UserDirectory userDirectory;
    private final EmailSender emailSender;

    public HandleEventUseCase(ProcessedEventRepository processedEvents, TemplateRepository templates,
                              NotificationRepository notifications, UserDirectory userDirectory,
                              EmailSender emailSender) {
        this.processedEvents = processedEvents;
        this.templates = templates;
        this.notifications = notifications;
        this.userDirectory = userDirectory;
        this.emailSender = emailSender;
    }

    public void handle(String eventId, String eventType, Map<String, Object> payload) {
        if (eventType == null || eventType.isBlank()) {
            log.warn("Event {} ignored: no type", eventId);
            return;
        }
        if (eventId != null && processedEvents.exists(eventId)) {
            log.info("Event {} already processed, ignored (idempotence)", eventId);
            return;
        }
        Optional<NotificationType> maybeType = toNotificationType(eventType);
        if (maybeType.isEmpty()) {
            log.warn("Event {} ignored: unsupported type '{}'", eventId, eventType);
            return;
        }
        NotificationType type = maybeType.get();
        Map<String, Object> data = payload == null ? Map.of() : payload;

        Template template = templates.findByCode(type.name()).orElseGet(() -> defaultTemplate(type));
        Map<String, String> variables = toVariables(data);
        String subject = template.renderSubject(variables);
        String body = template.renderBody(variables);

        Set<String> recipients = resolveRecipients(type, data);
        if (recipients.isEmpty()) {
            log.warn("Event {} ({}) has no recipient in payload {}", eventId, eventType, data.keySet());
        }
        for (String userId : recipients) {
            try {
                deliver(userId, type, subject, body);
            } catch (RuntimeException e) {
                log.error("Delivery failed for user {} (event {}): {}", userId, eventId, e.getMessage());
            }
        }
        if (eventId != null) {
            processedEvents.markProcessed(eventId);
        }
    }

    private void deliver(String userId, NotificationType type, String subject, String body) {
        // 1. notification dans l'application : toujours disponible
        notifications.save(Notification.builder()
                .userId(userId).type(type).channel(Channel.IN_APP)
                .status(NotificationStatus.SENT).message(body).createdAt(Instant.now()).build());

        // 2. e-mail : necessite les coordonnees du destinataire (appel synchrone a Utilisateurs)
        Notification email = notifications.save(Notification.builder()
                .userId(userId).type(type).channel(Channel.EMAIL)
                .status(NotificationStatus.PENDING).message(body).createdAt(Instant.now()).build());
        try {
            Optional<UserContact> contact = userDirectory.findContact(userId);
            if (contact.isEmpty()) {
                log.warn("User {} not found: e-mail notification FAILED", userId);
                email.markAsFailed();
            } else {
                emailSender.send(contact.get().email(), subject, body);
                email.markAsSent();
            }
        } catch (RuntimeException e) {
            log.warn("E-mail to user {} FAILED: {}", userId, e.getMessage());
            email.markAsFailed();
        }
        notifications.save(email);
    }

    private Set<String> resolveRecipients(NotificationType type, Map<String, Object> data) {
        Set<String> result = new LinkedHashSet<>();
        for (String key : SINGLE_KEYS) {
            addIfPresent(result, data.get(key));
        }
        if (type == NotificationType.USER_REGISTERED && result.isEmpty()) {
            addIfPresent(result, data.get("keycloakId"));
            addIfPresent(result, data.get("id"));
        }
        for (String key : LIST_KEYS) {
            Object value = data.get(key);
            if (value instanceof Collection<?> items) {
                items.forEach(item -> addIfPresent(result, item));
            }
        }
        return result;
    }

    private void addIfPresent(Set<String> target, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            target.add(String.valueOf(value));
        }
    }

    private Map<String, String> toVariables(Map<String, Object> data) {
        Map<String, String> variables = new HashMap<>();
        data.forEach((key, value) -> {
            if (value == null) return;
            if (value instanceof Collection<?> items) {
                StringJoiner joiner = new StringJoiner(", ");
                items.forEach(i -> joiner.add(String.valueOf(i)));
                variables.put(key, joiner.toString());
            } else {
                variables.put(key, String.valueOf(value));
            }
        });
        return variables;
    }

    private Optional<NotificationType> toNotificationType(String eventType) {
        try {
            return Optional.of(NotificationType.valueOf(eventType.trim().toUpperCase().replace('.', '_')));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Template defaultTemplate(NotificationType type) {
        return new Template(type.name(), "CampusRide", "Nouvelle notification : " + type.name());
    }
}
