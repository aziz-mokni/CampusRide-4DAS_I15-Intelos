package com.campusride.reputation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private final Instant now = Instant.parse("2026-11-20T09:00:00Z");
    private final CompletedTrip trip = CompletedTrip.of("t1", "driver", List.of("p1"), now);

    @Test
    void ratingMustBeBetweenOneAndFive() {
        assertThrows(IllegalArgumentException.class, () -> Review.of(trip, "p1", "driver", 0, null, now));
        assertThrows(IllegalArgumentException.class, () -> Review.of(trip, "p1", "driver", 6, null, now));
        assertEquals(5, Review.of(trip, "p1", "driver", 5, null, now).rating());
    }

    @Test
    void editKeepsIdentityAndChangesOnlyRatingAndComment() {
        Review review = Review.of(trip, "p1", "driver", 2, "meh", now);
        Review edited = review.edit(4, "fine");
        assertEquals(review.id(), edited.id());
        assertEquals(review.createdAt(), edited.createdAt());
        assertEquals(review.type(), edited.type());
        assertEquals(4, edited.rating());
        assertEquals("fine", edited.comment());
    }
}
