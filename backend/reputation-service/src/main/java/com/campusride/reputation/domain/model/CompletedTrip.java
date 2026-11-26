package com.campusride.reputation.domain.model;

import com.campusride.reputation.domain.exception.ForbiddenException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** A finished trip: opens the review period for its driver and passengers. */
public record CompletedTrip(
        String id,
        String tripId,
        String driverId,
        List<String> passengerIds,
        Instant completedAt,
        Instant reviewDeadline) {

    // ponytail: fixed review period, move to config if the team wants it tunable
    public static final Duration REVIEW_PERIOD = Duration.ofDays(7);

    public static CompletedTrip of(String tripId, String driverId, List<String> passengerIds, Instant completedAt) {
        if (passengerIds.contains(driverId)) {
            throw new IllegalArgumentException("The driver cannot also be a passenger");
        }
        return new CompletedTrip(UUID.randomUUID().toString(), tripId, driverId, List.copyOf(passengerIds),
                completedAt, completedAt.plus(REVIEW_PERIOD));
    }

    public boolean isParticipant(String userId) {
        return driverId.equals(userId) || passengerIds.contains(userId);
    }

    /** Checks that author may review target on this trip and returns the review direction. */
    public ReviewType reviewTypeFor(String authorId, String targetId, Instant now) {
        if (!now.isBefore(reviewDeadline)) {
            throw new ForbiddenException("The review period for this trip is over");
        }
        if (authorId.equals(targetId)) {
            throw new ForbiddenException("You cannot review yourself");
        }
        if (!isParticipant(authorId) || !isParticipant(targetId)) {
            throw new ForbiddenException("Author and target must both have taken part in the trip");
        }
        if (driverId.equals(authorId)) return ReviewType.DRIVER_TO_PASSENGER;
        if (driverId.equals(targetId)) return ReviewType.PASSENGER_TO_DRIVER;
        throw new ForbiddenException("A passenger can only review the driver");
    }
}
