package com.campusride.reputation.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    void averagesTheRatings() {
        Score score = Score.empty("u").withRatings(List.of(5, 4, 3));
        assertEquals(4.0, score.averageRating());
        assertEquals(3, score.reviewCount());
        assertEquals(4.0, score.trustScore());
    }

    @Test
    void validatedReportsLowerTrustButNeverBelowZero() {
        Score score = Score.empty("u").withRatings(List.of(2)).withValidatedReport();
        assertEquals(1, score.validatedReports());
        assertEquals(2 - Score.REPORT_PENALTY, score.trustScore());
        for (int i = 0; i < 10; i++) score = score.withValidatedReport();
        assertEquals(0, score.trustScore());
    }

    @Test
    void reportsSurviveARecalculationOfTheRatings() {
        Score score = Score.empty("u").withValidatedReport().withRatings(List.of(5));
        assertEquals(1, score.validatedReports());
        assertEquals(5 - Score.REPORT_PENALTY, score.trustScore());
    }

    @Test
    void noReviewsMeansZeroEverywhere() {
        Score score = Score.empty("u").withRatings(List.of(5)).withRatings(List.of());
        assertEquals(0, score.averageRating());
        assertEquals(0, score.reviewCount());
        assertEquals(0, score.trustScore());
    }
}
