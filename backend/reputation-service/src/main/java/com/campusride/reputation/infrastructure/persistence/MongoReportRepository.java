package com.campusride.reputation.infrastructure.persistence;

import com.campusride.reputation.application.port.ReportRepository;
import com.campusride.reputation.domain.model.Report;
import org.springframework.data.repository.Repository;

/** Spring Data generates the implementation of the port. */
interface MongoReportRepository extends Repository<Report, String>, ReportRepository {
}
