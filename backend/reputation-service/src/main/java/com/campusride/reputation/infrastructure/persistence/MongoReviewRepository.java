package com.campusride.reputation.infrastructure.persistence;

import com.campusride.reputation.application.port.ReviewRepository;
import com.campusride.reputation.domain.model.Review;
import org.springframework.data.repository.Repository;

/** Spring Data generates the implementation of the port. */
interface MongoReviewRepository extends Repository<Review, String>, ReviewRepository {
}
