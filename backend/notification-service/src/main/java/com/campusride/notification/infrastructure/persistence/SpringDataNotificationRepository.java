package com.campusride.notification.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataNotificationRepository extends MongoRepository<NotificationDocument, String> {}
