package com.campusride.notification.infrastructure.mail;

import com.campusride.notification.domain.port.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmtpEmailSender implements EmailSender {

    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean enabled;
    private final String from;

    public SmtpEmailSender(ObjectProvider<JavaMailSender> mailSender,
                           @Value("${notification.mail.enabled:false}") boolean enabled,
                           @Value("${notification.mail.from:no-reply@campusride.tn}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    @Override
    public void send(String to, String subject, String body) {
        if (!enabled) {
            log.info("[EMAIL simulated] to={} subject='{}' body='{}'", to, subject, body);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.getObject().send(message);
    }
}
