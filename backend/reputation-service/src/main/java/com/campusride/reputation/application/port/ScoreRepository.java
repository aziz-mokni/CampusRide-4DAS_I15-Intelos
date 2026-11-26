package com.campusride.reputation.application.port;

import com.campusride.reputation.domain.model.Score;

import java.util.Optional;

public interface ScoreRepository {
    Score save(Score score);
    Optional<Score> findByUserId(String userId);
}
