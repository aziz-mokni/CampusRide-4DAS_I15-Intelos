package com.campusride.userauth.infrastructure.messaging;

import com.campusride.userauth.application.port.in.ManageUserUseCase;
import com.campusride.userauth.domain.exception.UserNotFoundException;
import com.campusride.userauth.domain.model.UserStatus;
import com.campusride.userauth.infrastructure.config.RabbitConfig;
import com.campusride.userauth.infrastructure.persistence.ProcessedEventEntity;
import com.campusride.userauth.infrastructure.persistence.ProcessedEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Consomme block.requested (publié par Réputation) et bloque l'utilisateur concerné. */
@Component
public class BlockRequestedListener {

    private static final Logger log = LoggerFactory.getLogger(BlockRequestedListener.class);

    private final ManageUserUseCase users;
    private final ProcessedEventJpaRepository processed;

    public BlockRequestedListener(ManageUserUseCase users, ProcessedEventJpaRepository processed) {
        this.users = users;
        this.processed = processed;
    }

    @RabbitListener(queues = RabbitConfig.BLOCK_REQUESTS_QUEUE)
    @Transactional
    public void onBlockRequested(EventEnvelope event) {
        if (event == null || event.eventId() == null) {
            log.warn("[EVENT] block.requested ignoré : message sans eventId");
            return;
        }
        if (processed.existsById(event.eventId())) {
            log.info("[EVENT] block.requested déjà traité eventId={}", event.eventId());
            return;
        }
        Object userId = event.payload() == null ? null : event.payload().get("userId");
        if (userId == null) {
            log.warn("[EVENT] block.requested ignoré eventId={} : payload sans userId", event.eventId());
            return;
        }
        try {
            users.changeStatus(userId.toString(), UserStatus.BLOCKED);
            log.info("[EVENT] block.requested traité eventId={} userId={}", event.eventId(), userId);
        } catch (UserNotFoundException e) {
            log.warn("[EVENT] block.requested eventId={} : {}", event.eventId(), e.getMessage());
        }
        processed.save(new ProcessedEventEntity(event.eventId()));
    }
}
