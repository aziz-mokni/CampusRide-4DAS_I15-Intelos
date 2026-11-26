package com.campusride.notification.domain.port;

public interface EmailSender {
    void send(String to, String subject, String body);
}
