package com.campusride.userauth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, String> {
    boolean existsByPlateNumberAndIdNot(String plateNumber, String id);
}
