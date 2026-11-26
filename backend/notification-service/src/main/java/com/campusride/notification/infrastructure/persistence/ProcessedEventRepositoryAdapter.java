package com.campusride.notification.infrastructure.persistence;

import com.campusride.notification.domain.port.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final SpringDataProcessedEventRepository repo;

    @Override
    public boolean exists(String eventId) { return repo.existsById(eventId); }

    @Override
    public void markProcessed(String eventId) {
        repo.save(new ProcessedEventDocument(eventId, Instant.now()));
    }
}
