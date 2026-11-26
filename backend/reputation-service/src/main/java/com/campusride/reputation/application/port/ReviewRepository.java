package com.campusride.reputation.application.port;

import com.campusride.reputation.domain.model.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(String id);
    Page<Review> findByTargetId(String targetId, Pageable pageable);
    List<Review> findByTargetId(String targetId);
    boolean existsByTripIdAndAuthorIdAndTargetId(String tripId, String authorId, String targetId);
    void deleteById(String id);
}
