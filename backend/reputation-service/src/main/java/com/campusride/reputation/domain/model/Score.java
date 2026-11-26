package com.campusride.reputation.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Reputation of one user, derived from the reviews they received and their validated reports. */
public record Score(
        String id,
        String userId,
        double averageRating,
        int reviewCount,
        int validatedReports,
        double trustScore,
        Instant updatedAt) {

    // ponytail: naive trust formula (each validated report costs 0.5 star), refine with the team
    static final double REPORT_PENALTY = 0.5;

    public static Score empty(String userId) {
        return new Score(UUID.randomUUID().toString(), userId, 0, 0, 0, 0, Instant.now());
    }

    public Score withRatings(List<Integer> ratings) {
        double average = ratings.stream().mapToInt(Integer::intValue).average().orElse(0);
        return recompute(average, ratings.size(), validatedReports);
    }

    public Score withValidatedReport() {
        return recompute(averageRating, reviewCount, validatedReports + 1);
    }

    private Score recompute(double average, int count, int reports) {
        double trust = count == 0 ? 0 : Math.max(0, average - REPORT_PENALTY * reports);
        return new Score(id, userId, average, count, reports, trust, Instant.now());
    }
}
