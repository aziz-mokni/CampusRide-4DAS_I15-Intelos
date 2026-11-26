package com.campusride.reputation.domain.model;

import com.campusride.reputation.domain.exception.ConflictException;

import java.time.Instant;
import java.util.UUID;

/** Penalty applied after an accepted report. WARNING and BAN have no end date, SUSPENSION needs one. */
public record Sanction(
        String id,
        String reportId,
        String userId,
        SanctionType type,
        String reason,
        Instant startDate,
        Instant endDate) {

    public static Sanction apply(Report report, SanctionType type, String reason, Instant endDate) {
        if (report.status() != ReportStatus.ACCEPTED) {
            throw new ConflictException("Only an accepted report can lead to a sanction");
        }
        Instant now = Instant.now();
        if (type == SanctionType.SUSPENSION && (endDate == null || !endDate.isAfter(now))) {
            throw new IllegalArgumentException("A suspension needs an end date in the future");
        }
        Instant end = type == SanctionType.SUSPENSION ? endDate : null;
        return new Sanction(UUID.randomUUID().toString(), report.id(), report.reportedUserId(), type, reason, now, end);
    }
}
