package com.campusride.notification.infrastructure.persistence;

import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.domain.port.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository repo;
    private final MongoTemplate mongoTemplate;

    @Override
    public Notification save(Notification n) {
        return toDomain(repo.save(toDocument(n)));
    }

    @Override
    public Optional<Notification> findById(String id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Notification> findByUserId(String userId, NotificationStatus status, Channel channel, int page, int size) {
        Query query = new Query(Criteria.where("userId").is(userId));
        if (status != null) query.addCriteria(Criteria.where("status").is(status));
        if (channel != null) query.addCriteria(Criteria.where("channel").is(channel));
        query.with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return mongoTemplate.find(query, NotificationDocument.class).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    private NotificationDocument toDocument(Notification n) {
        return NotificationDocument.builder()
                .id(n.getId()).userId(n.getUserId()).type(n.getType())
                .channel(n.getChannel()).status(n.getStatus())
                .message(n.getMessage()).createdAt(n.getCreatedAt()).build();
    }

    private Notification toDomain(NotificationDocument d) {
        return Notification.builder()
                .id(d.getId()).userId(d.getUserId()).type(d.getType())
                .channel(d.getChannel()).status(d.getStatus())
                .message(d.getMessage()).createdAt(d.getCreatedAt()).build();
    }
}
