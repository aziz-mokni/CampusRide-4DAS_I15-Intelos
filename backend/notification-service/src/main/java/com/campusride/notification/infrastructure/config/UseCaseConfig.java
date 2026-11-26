package com.campusride.notification.infrastructure.config;

import com.campusride.notification.application.usecase.*;
import com.campusride.notification.domain.port.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Les cas d'utilisation sont des classes Java pures : c'est ici qu'on les declare comme beans. */
@Configuration
public class UseCaseConfig {

    @Bean
    HandleEventUseCase handleEventUseCase(ProcessedEventRepository processed, TemplateRepository templates,
                                          NotificationRepository notifications, UserDirectory users, EmailSender mail) {
        return new HandleEventUseCase(processed, templates, notifications, users, mail);
    }

    @Bean ListNotificationsUseCase listNotificationsUseCase(NotificationRepository r) { return new ListNotificationsUseCase(r); }
    @Bean GetNotificationUseCase getNotificationUseCase(NotificationRepository r) { return new GetNotificationUseCase(r); }
    @Bean MarkNotificationAsReadUseCase markNotificationAsReadUseCase(NotificationRepository r) { return new MarkNotificationAsReadUseCase(r); }
    @Bean DeleteNotificationUseCase deleteNotificationUseCase(NotificationRepository r) { return new DeleteNotificationUseCase(r); }
    @Bean CreateNotificationUseCase createNotificationUseCase(NotificationRepository r) { return new CreateNotificationUseCase(r); }

    @Bean CreateConversationUseCase createConversationUseCase(ConversationRepository r) { return new CreateConversationUseCase(r); }
    @Bean ListConversationsUseCase listConversationsUseCase(ConversationRepository r) { return new ListConversationsUseCase(r); }
    @Bean GetMessagesUseCase getMessagesUseCase(ConversationRepository r) { return new GetMessagesUseCase(r); }
    @Bean SendMessageUseCase sendMessageUseCase(ConversationRepository r) { return new SendMessageUseCase(r); }
}
