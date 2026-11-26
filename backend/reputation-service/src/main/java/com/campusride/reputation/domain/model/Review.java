package com.campusride.reputation.domain.model;

import java.time.Instant;
import java.util.UUID;

/** One review per (trip, author, target). */
public record Review(
        String id,
        String tripId,
        String authorId,
        String targetId,
        ReviewType type,
        int rating,
        String comment,
        Instant createdAt) {

    public Review {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    public static Review of(CompletedTrip trip, String authorId, String targetId, int rating, String comment, Instant now) {
        ReviewType type = trip.reviewTypeFor(authorId, targetId, now);
        return new Review(UUID.randomUUID().toString(), trip.tripId(), authorId, targetId, type, rating, comment, now);
    }

    public Review edit(int rating, String comment) {
        return new Review(id, tripId, authorId, targetId, type, rating, comment, createdAt);
    }
}
