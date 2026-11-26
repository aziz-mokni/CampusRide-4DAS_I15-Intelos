package com.campusride.userauth.infrastructure.persistence;

import com.campusride.userauth.application.port.out.UserRepositoryPort;
import com.campusride.userauth.domain.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;
    private final VehicleJpaRepository vehicles;

    public UserRepositoryAdapter(UserJpaRepository jpa, VehicleJpaRepository vehicles) {
        this.jpa = jpa;
        this.vehicles = vehicles;
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
    public Optional<User> findByKeycloakId(String keycloakId) {
        return jpa.findByKeycloakId(keycloakId).map(UserPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll(int page, int size) {
        return jpa.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream().map(UserPersistenceMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpa.count();
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

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPlateNumberExcludingVehicle(String plateNumber, String vehicleId) {
        return vehicles.existsByPlateNumberAndIdNot(plateNumber, vehicleId);
    }
}
