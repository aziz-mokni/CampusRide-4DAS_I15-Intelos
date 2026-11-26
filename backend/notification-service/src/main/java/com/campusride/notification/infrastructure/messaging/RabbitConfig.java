package com.campusride.notification.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "covoiturage.events";
    public static final String QUEUE = "notification.queue";

    /** Evenements consommes par le service (section 6.2 du dossier technique). */
    public static final List<String> ROUTING_KEYS = List.of(
            "user.registered",
            "booking.confirmed", "booking.rejected", "booking.cancelled",
            "payment.completed", "payment.failed",
            "trip.cancelled",
            "review.published", "sanction.applied");

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Declarables notificationBindings(Queue notificationQueue, TopicExchange eventsExchange) {
        List<Binding> bindings = ROUTING_KEYS.stream()
                .map(key -> BindingBuilder.bind(notificationQueue).to(eventsExchange).with(key))
                .toList();
        return new Declarables(bindings);
    }

    /** Messages JSON (Jackson 3, Spring Boot 4). */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
