package com.campusride.reputation.infrastructure.persistence;

import com.campusride.reputation.application.port.SanctionRepository;
import com.campusride.reputation.domain.model.Sanction;
import org.springframework.data.repository.Repository;

/** Spring Data generates the implementation of the port. */
interface MongoSanctionRepository extends Repository<Sanction, String>, SanctionRepository {
}
