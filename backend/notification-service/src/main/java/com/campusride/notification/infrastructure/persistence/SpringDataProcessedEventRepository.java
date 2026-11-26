package com.campusride.notification.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataProcessedEventRepository extends MongoRepository<ProcessedEventDocument, String> {}
