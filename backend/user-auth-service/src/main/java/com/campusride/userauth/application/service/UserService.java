package com.campusride.userauth.application.service;

import com.campusride.userauth.application.port.in.*;
import com.campusride.userauth.application.port.out.*;
import com.campusride.userauth.domain.exception.*;
import com.campusride.userauth.domain.model.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserService implements ManageUserUseCase {

    private final UserRepositoryPort users;
    private final EventPublisherPort events;

    public UserService(UserRepositoryPort users, EventPublisherPort events) {
        this.users = users;
        this.events = events;
    }

    @Override
    public User create(CreateUserCommand c) {
        if (users.existsByEmail(c.email())) {
            throw new EmailAlreadyUsedException(c.email());
        }
        Profile profile = new Profile(UUID.randomUUID().toString(), c.firstName(),
                c.lastName(), c.phone(), c.university(), c.photoUrl(), c.bio());
        User user = new User(UUID.randomUUID().toString(), c.keycloakId(), c.email(),
                Role.USER, UserStatus.ACTIVE, Instant.now(), profile);
        User saved = users.save(user);
        events.publishUserRegistered(saved);
        return saved;
    }

    @Override
    public User getById(String id) {
        return users.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getByKeycloakId(String keycloakId) {
        return users.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));
    }

    @Override
    public List<User> list(int page, int size) {
        return users.findAll(page, size);
    }

    @Override
    public long count() {
        return users.count();
    }

    @Override
    public User updateProfile(String id, UpdateProfileCommand c) {
        User user = getById(id);
        user.getProfile().update(c.firstName(), c.lastName(), c.phone(),
                c.university(), c.photoUrl(), c.bio());
        return users.save(user);
    }

    @Override
    public User changeStatus(String id, UserStatus status) {
        User user = getById(id);
        if (user.getStatus() == status) {
            return user;   // rien à changer : pas de nouvel événement user.blocked
        }
        if (status == UserStatus.BLOCKED) {
            user.block();
        } else {
            user.activate();
        }
        User saved = users.save(user);
        if (status == UserStatus.BLOCKED) {
            events.publishUserBlocked(saved);
        }
        return saved;
    }
}
