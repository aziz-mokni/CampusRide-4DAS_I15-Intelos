package com.campusride.notification.infrastructure.messaging;

import com.campusride.notification.application.usecase.HandleEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final HandleEventUseCase handleEvent;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onEvent(EventEnvelope event) {
        log.info("Event received: type={} eventId={}", event.type(), event.eventId());
        handleEvent.handle(event.eventId(), event.type(), event.payload());
    }
}
