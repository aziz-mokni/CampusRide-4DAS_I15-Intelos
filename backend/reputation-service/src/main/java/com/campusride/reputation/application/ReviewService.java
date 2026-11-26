package com.campusride.reputation.application;

import com.campusride.reputation.application.port.CompletedTripRepository;
import com.campusride.reputation.application.port.ReviewRepository;
import com.campusride.reputation.application.port.ScoreRepository;
import com.campusride.reputation.domain.exception.ConflictException;
import com.campusride.reputation.domain.exception.ForbiddenException;
import com.campusride.reputation.domain.exception.NotFoundException;
import com.campusride.reputation.domain.model.Review;
import com.campusride.reputation.domain.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Reviews and the trust score they feed. */
@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final CompletedTripRepository trips;
    private final ScoreRepository scores;

    public ReviewService(ReviewRepository reviews, CompletedTripRepository trips, ScoreRepository scores) {
        this.reviews = reviews;
        this.trips = trips;
        this.scores = scores;
    }

    public Review publish(String tripId, String authorId, String targetId, int rating, String comment) {
        // The contract has no 404 on POST /reviews: an unknown trip is simply not reviewable
        var trip = trips.findByTripId(tripId)
                .orElseThrow(() -> new ForbiddenException("Trip " + tripId + " is not completed"));
        if (reviews.existsByTripIdAndAuthorIdAndTargetId(tripId, authorId, targetId)) {
            throw new ConflictException("You already reviewed this user for this trip");
        }
        Review saved = reviews.save(Review.of(trip, authorId, targetId, rating, comment, Instant.now()));
        refreshScore(targetId);
        return saved;
    }

    public Review get(String id) {
        return reviews.findById(id).orElseThrow(() -> new NotFoundException("Review " + id + " not found"));
    }

    public Page<Review> receivedBy(String userId, Pageable pageable) {
        return reviews.findByTargetId(userId, pageable);
    }

    public Review edit(String id, int rating, String comment) {
        Review updated = reviews.save(get(id).edit(rating, comment));
        refreshScore(updated.targetId());
        return updated;
    }

    public void delete(String id) {
        Review review = get(id);
        reviews.deleteById(id);
        refreshScore(review.targetId());
    }

    public Score scoreOf(String userId) {
        return scores.findByUserId(userId).orElseThrow(() -> new NotFoundException("No score for user " + userId));
    }

    private void refreshScore(String userId) {
        List<Integer> ratings = reviews.findByTargetId(userId).stream().map(Review::rating).toList();
        Score score = scores.findByUserId(userId).orElseGet(() -> Score.empty(userId));
        scores.save(score.withRatings(ratings));
    }
}
