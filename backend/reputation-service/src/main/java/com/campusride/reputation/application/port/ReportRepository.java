package com.campusride.reputation.application.port;

import com.campusride.reputation.domain.model.Report;
import com.campusride.reputation.domain.model.ReportStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findById(String id);
    Page<Report> findAll(Pageable pageable);
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    void deleteById(String id);
}
