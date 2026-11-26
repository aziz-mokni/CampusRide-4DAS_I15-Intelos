package com.campusride.reputation.infrastructure.persistence;

import com.campusride.reputation.application.port.ScoreRepository;
import com.campusride.reputation.domain.model.Score;
import org.springframework.data.repository.Repository;

/** Spring Data generates the implementation of the port. */
interface MongoScoreRepository extends Repository<Score, String>, ScoreRepository {
}
