package com.campusride.reputation.domain.model;

import com.campusride.reputation.domain.exception.ForbiddenException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompletedTripTest {

    private final Instant completedAt = Instant.parse("2026-11-20T08:00:00Z");
    private final CompletedTrip trip = CompletedTrip.of("t1", "driver", List.of("p1", "p2"), completedAt);
    private final Instant duringPeriod = completedAt.plusSeconds(3600);

    @Test
    void reviewDeadlineIsCompletionPlusReviewPeriod() {
        assertEquals(completedAt.plus(CompletedTrip.REVIEW_PERIOD), trip.reviewDeadline());
    }

    @Test
    void driverCannotAlsoBeAPassenger() {
        assertThrows(IllegalArgumentException.class, () -> CompletedTrip.of("t2", "driver", List.of("driver"), completedAt));
    }

    @Test
    void reviewDirectionDependsOnWhoIsTheDriver() {
        assertEquals(ReviewType.PASSENGER_TO_DRIVER, trip.reviewTypeFor("p1", "driver", duringPeriod));
        assertEquals(ReviewType.DRIVER_TO_PASSENGER, trip.reviewTypeFor("driver", "p2", duringPeriod));
    }

    @Test
    void rejectsInvalidReviewers() {
        assertThrows(ForbiddenException.class, () -> trip.reviewTypeFor("p1", "p1", duringPeriod), "self review");
        assertThrows(ForbiddenException.class, () -> trip.reviewTypeFor("p1", "p2", duringPeriod), "passenger to passenger");
        assertThrows(ForbiddenException.class, () -> trip.reviewTypeFor("stranger", "driver", duringPeriod), "not a participant");
        assertThrows(ForbiddenException.class, () -> trip.reviewTypeFor("p1", "stranger", duringPeriod), "target not a participant");
    }

    @Test
    void reviewPeriodEndsAtTheDeadline() {
        assertThrows(ForbiddenException.class, () -> trip.reviewTypeFor("p1", "driver", trip.reviewDeadline()));
    }
}
