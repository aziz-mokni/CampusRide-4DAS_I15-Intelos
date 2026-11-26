package com.campusride.reputation.infrastructure.persistence;

import com.campusride.reputation.application.port.CompletedTripRepository;
import com.campusride.reputation.domain.model.CompletedTrip;
import org.springframework.data.repository.Repository;

/** Spring Data generates the implementation of the port. */
interface MongoCompletedTripRepository extends Repository<CompletedTrip, String>, CompletedTripRepository {
}
