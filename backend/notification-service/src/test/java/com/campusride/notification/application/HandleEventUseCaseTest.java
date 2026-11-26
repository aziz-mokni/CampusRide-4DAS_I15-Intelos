package com.campusride.notification.application;

import com.campusride.notification.application.usecase.HandleEventUseCase;
import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.model.Template;
import com.campusride.notification.domain.model.UserContact;
import com.campusride.notification.domain.port.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HandleEventUseCaseTest {

    /** Depot en memoire : pas besoin de MongoDB pour tester la logique metier. */
    static class InMemoryNotifications implements NotificationRepository {
        final List<Notification> store = new ArrayList<>();

        @Override
        public Notification save(Notification n) {
            if (n.getId() == null) {
                n.setId(UUID.randomUUID().toString());
                store.add(n);
            }
            return n;
        }

        @Override
        public Optional<Notification> findById(String id) {
            return store.stream().filter(n -> id.equals(n.getId())).findFirst();
        }

        @Override
        public List<Notification> findByUserId(String userId, NotificationStatus status, Channel channel, int page, int size) {
            return store.stream().filter(n -> userId.equals(n.getUserId())).toList();
        }

        @Override
        public void deleteById(String id) {
            store.removeIf(n -> id.equals(n.getId()));
        }
    }

    static class InMemoryProcessedEvents implements ProcessedEventRepository {
        final Set<String> ids = new HashSet<>();

        @Override public boolean exists(String eventId) { return ids.contains(eventId); }
        @Override public void markProcessed(String eventId) { ids.add(eventId); }
    }

    static class NoTemplates implements TemplateRepository {
        @Override public Optional<Template> findByCode(String code) { return Optional.empty(); }
        @Override public Template save(Template template) { return template; }
    }

    private InMemoryNotifications notifications;
    private InMemoryProcessedEvents processed;
    private final List<String> sentEmails = new ArrayList<>();

    @BeforeEach
    void setUp() {
        notifications = new InMemoryNotifications();
        processed = new InMemoryProcessedEvents();
        sentEmails.clear();
    }

    private HandleEventUseCase useCase(UserDirectory directory) {
        EmailSender sender = (to, subject, body) -> sentEmails.add(to);
        return new HandleEventUseCase(processed, new NoTemplates(), notifications, directory, sender);
    }

    private Notification byChannel(Channel channel) {
        return notifications.store.stream().filter(n -> n.getChannel() == channel).findFirst().orElseThrow();
    }

    @Test
    void userFound_inAppAndEmailAreSent() {
        HandleEventUseCase uc = useCase(id -> Optional.of(new UserContact("sara@esprit.tn", "Sara")));

        uc.handle("evt-1", "booking.confirmed", Map.of("bookingId", "b-101", "passengerId", "u-7"));

        assertEquals(2, notifications.store.size());
        assertEquals(NotificationStatus.SENT, byChannel(Channel.IN_APP).getStatus());
        assertEquals(NotificationStatus.SENT, byChannel(Channel.EMAIL).getStatus());
        assertEquals(List.of("sara@esprit.tn"), sentEmails);
    }

    @Test
    void userNotFound_emailNotificationFails() {
        HandleEventUseCase uc = useCase(id -> Optional.empty());

        uc.handle("evt-2", "booking.confirmed", Map.of("bookingId", "b-101", "passengerId", "ghost"));

        assertEquals(NotificationStatus.SENT, byChannel(Channel.IN_APP).getStatus());
        assertEquals(NotificationStatus.FAILED, byChannel(Channel.EMAIL).getStatus());
        assertTrue(sentEmails.isEmpty());
    }

    @Test
    void sameEventTwice_isProcessedOnlyOnce() {
        HandleEventUseCase uc = useCase(id -> Optional.of(new UserContact("sara@esprit.tn", "Sara")));
        Map<String, Object> payload = Map.of("bookingId", "b-101", "passengerId", "u-7");

        uc.handle("evt-3", "booking.confirmed", payload);
        uc.handle("evt-3", "booking.confirmed", payload);

        assertEquals(2, notifications.store.size());
        assertEquals(1, sentEmails.size());
    }

    @Test
    void unknownEventType_isIgnored() {
        HandleEventUseCase uc = useCase(id -> Optional.empty());

        uc.handle("evt-4", "seats.reserved", Map.of("tripId", "t-1"));

        assertTrue(notifications.store.isEmpty());
    }
}
