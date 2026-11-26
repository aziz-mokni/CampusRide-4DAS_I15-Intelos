package com.campusride.reputation.infrastructure.persistence;

import com.campusride.reputation.domain.model.CompletedTrip;
import com.campusride.reputation.domain.model.Report;
import com.campusride.reputation.domain.model.Review;
import com.campusride.reputation.domain.model.Sanction;
import com.campusride.reputation.domain.model.Score;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

/** Indexes live here so the domain records stay free of Mongo annotations. */
@Component
class MongoIndexes implements ApplicationRunner {

    private final MongoTemplate mongo;

    MongoIndexes(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public void run(ApplicationArguments args) {
        mongo.indexOps(CompletedTrip.class).createIndex(new Index("tripId", Direction.ASC).unique());
        mongo.indexOps(Score.class).createIndex(new Index("userId", Direction.ASC).unique());
        // One review per author, target and trip, even under concurrent requests
        mongo.indexOps(Review.class).createIndex(new Index("tripId", Direction.ASC)
                .on("authorId", Direction.ASC).on("targetId", Direction.ASC).unique());
        mongo.indexOps(Review.class).createIndex(new Index("targetId", Direction.ASC));
        mongo.indexOps(Report.class).createIndex(new Index("status", Direction.ASC));
        mongo.indexOps(Sanction.class).createIndex(new Index("userId", Direction.ASC));
    }
}
