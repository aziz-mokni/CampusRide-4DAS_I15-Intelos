package com.campusride.reputation.domain.model;

import com.campusride.reputation.domain.exception.ConflictException;

import java.time.Instant;
import java.util.UUID;

/** A user's complaint about another user, handled by a moderator. */
public record Report(
        String id,
        String reporterId,
        String reportedUserId,
        String tripId,
        ReportReason reason,
        String description,
        ReportStatus status,
        String moderatorId,
        Instant createdAt,
        Instant decidedAt) {

    public static Report open(String reporterId, String reportedUserId, String tripId, ReportReason reason, String description) {
        if (reporterId.equals(reportedUserId)) {
            throw new IllegalArgumentException("You cannot report yourself");
        }
        return new Report(UUID.randomUUID().toString(), reporterId, reportedUserId, tripId, reason, description,
                ReportStatus.PENDING, null, Instant.now(), null);
    }

    public boolean isDecided() {
        return status == ReportStatus.ACCEPTED || status == ReportStatus.REJECTED;
    }

    /** PENDING -> UNDER_REVIEW -> ACCEPTED | REJECTED. A decided report is final. */
    public Report moveTo(ReportStatus newStatus, String moderatorId) {
        if (isDecided()) {
            throw new ConflictException("This report has already been decided");
        }
        if (newStatus == ReportStatus.PENDING) {
            throw new IllegalArgumentException("A report cannot go back to PENDING");
        }
        Instant decided = newStatus == ReportStatus.UNDER_REVIEW ? null : Instant.now();
        return new Report(id, reporterId, reportedUserId, tripId, reason, description, newStatus, moderatorId, createdAt, decided);
    }
}
