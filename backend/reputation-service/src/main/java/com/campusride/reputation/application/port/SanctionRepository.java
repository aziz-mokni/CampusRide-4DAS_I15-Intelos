package com.campusride.reputation.application.port;

import com.campusride.reputation.domain.model.Sanction;

import java.util.List;
import java.util.Optional;

public interface SanctionRepository {
    Sanction save(Sanction sanction);
    Optional<Sanction> findById(String id);
    List<Sanction> findByUserIdOrderByStartDateDesc(String userId);
}
