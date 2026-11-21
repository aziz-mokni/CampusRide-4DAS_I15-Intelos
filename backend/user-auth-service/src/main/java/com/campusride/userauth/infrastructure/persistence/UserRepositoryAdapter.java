package com.campusride.userauth.infrastructure.persistence;

import com.campusride.userauth.application.port.out.UserRepositoryPort;
import com.campusride.userauth.domain.model.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public User save(User user) {
        return UserPersistenceMapper.toDomain(jpa.save(UserPersistenceMapper.toEntity(user)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return jpa.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPlateNumber(String plateNumber) {
        return jpa.existsByVehiclesPlateNumber(plateNumber);
    }
}