package com.campusride.userauth.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "covoiturage.events";
    public static final String BLOCK_REQUESTS_QUEUE = "users.block-requests";
    public static final String BLOCK_REQUESTED_KEY = "block.requested";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue blockRequestsQueue() {
        return QueueBuilder.durable(BLOCK_REQUESTS_QUEUE).build();
    }

    @Bean
    public Binding blockRequestsBinding(Queue blockRequestsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(blockRequestsQueue).to(eventsExchange).with(BLOCK_REQUESTED_KEY);
    }

    /**
     * JSON partout. Le type est déduit du paramètre de la méthode d'écoute (INFERRED),
     * pour que les messages envoyés par les autres services soient lus sans dépendre
     * du nom de leur classe Java.
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
