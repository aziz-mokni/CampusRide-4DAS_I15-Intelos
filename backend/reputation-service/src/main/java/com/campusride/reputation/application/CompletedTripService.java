package com.campusride.reputation.application;

import com.campusride.reputation.application.port.CompletedTripRepository;
import com.campusride.reputation.domain.exception.ConflictException;
import com.campusride.reputation.domain.exception.NotFoundException;
import com.campusride.reputation.domain.model.CompletedTrip;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Completed trips: created by hand for now, later by the trip.completed consumer. */
@Service
public class CompletedTripService {

    private final CompletedTripRepository trips;

    public CompletedTripService(CompletedTripRepository trips) {
        this.trips = trips;
    }

    public CompletedTrip record(String tripId, String driverId, List<String> passengerIds, Instant completedAt) {
        if (trips.findByTripId(tripId).isPresent()) {
            throw new ConflictException("Trip " + tripId + " is already recorded as completed");
        }
        return trips.save(CompletedTrip.of(tripId, driverId, passengerIds, completedAt));
    }

    public CompletedTrip get(String tripId) {
        return trips.findByTripId(tripId)
                .orElseThrow(() -> new NotFoundException("Completed trip " + tripId + " not found"));
    }

    public void delete(String tripId) {
        get(tripId);
        trips.deleteByTripId(tripId);
    }
}
