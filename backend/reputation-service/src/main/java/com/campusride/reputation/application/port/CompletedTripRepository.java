package com.campusride.reputation.application.port;

import com.campusride.reputation.domain.model.CompletedTrip;

import java.util.Optional;

public interface CompletedTripRepository {
    CompletedTrip save(CompletedTrip trip);
    Optional<CompletedTrip> findByTripId(String tripId);
    void deleteByTripId(String tripId);
}
