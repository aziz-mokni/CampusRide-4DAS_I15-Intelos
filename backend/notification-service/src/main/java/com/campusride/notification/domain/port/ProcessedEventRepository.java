package com.campusride.notification.domain.port;

public interface ProcessedEventRepository {
    boolean exists(String eventId);
    void markProcessed(String eventId);
}
