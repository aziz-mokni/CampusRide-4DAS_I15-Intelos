package com.campusride.reputation.application;

import com.campusride.reputation.application.port.ReportRepository;
import com.campusride.reputation.application.port.SanctionRepository;
import com.campusride.reputation.application.port.ScoreRepository;
import com.campusride.reputation.domain.exception.ConflictException;
import com.campusride.reputation.domain.exception.NotFoundException;
import com.campusride.reputation.domain.model.Report;
import com.campusride.reputation.domain.model.ReportReason;
import com.campusride.reputation.domain.model.ReportStatus;
import com.campusride.reputation.domain.model.Sanction;
import com.campusride.reputation.domain.model.SanctionType;
import com.campusride.reputation.domain.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Reports and the sanctions a moderator decides on them. */
@Service
public class ModerationService {

    /** Result of PATCH /reports/{id}: the sanction is null when none was applied. */
    public record Decision(Report report, Sanction sanction) {}

    private final ReportRepository reports;
    private final SanctionRepository sanctions;
    private final ScoreRepository scores;

    public ModerationService(ReportRepository reports, SanctionRepository sanctions, ScoreRepository scores) {
        this.reports = reports;
        this.sanctions = sanctions;
        this.scores = scores;
    }

    public Report report(String reporterId, String reportedUserId, String tripId, ReportReason reason, String description) {
        return reports.save(Report.open(reporterId, reportedUserId, tripId, reason, description));
    }

    public Report get(String id) {
        return reports.findById(id).orElseThrow(() -> new NotFoundException("Report " + id + " not found"));
    }

    public Page<Report> list(ReportStatus status, Pageable pageable) {
        return status == null ? reports.findAll(pageable) : reports.findByStatus(status, pageable);
    }

    /** Moves the report forward; when accepted, counts it in the score and optionally applies a sanction. */
    public Decision decide(String id, ReportStatus status, String moderatorId,
                           SanctionType sanctionType, String sanctionReason, Instant sanctionEnd) {
        if (sanctionType != null && status != ReportStatus.ACCEPTED) {
            throw new IllegalArgumentException("A sanction can only be applied when accepting a report");
        }
        Report report = get(id).moveTo(status, moderatorId);
        // Build the sanction before saving anything, so an invalid one leaves the report untouched
        Sanction sanction = sanctionType == null ? null : Sanction.apply(report, sanctionType, sanctionReason, sanctionEnd);

        reports.save(report);
        if (status == ReportStatus.ACCEPTED) {
            String userId = report.reportedUserId();
            Score score = scores.findByUserId(userId).orElseGet(() -> Score.empty(userId));
            scores.save(score.withValidatedReport());
        }
        if (sanction != null) {
            sanction = sanctions.save(sanction);
        }
        return new Decision(report, sanction);
    }

    public void delete(String id) {
        if (get(id).status() == ReportStatus.ACCEPTED) {
            throw new ConflictException("An accepted report is kept as the basis of the user's score and sanctions");
        }
        reports.deleteById(id);
    }

    public Sanction sanction(String id) {
        return sanctions.findById(id).orElseThrow(() -> new NotFoundException("Sanction " + id + " not found"));
    }

    public List<Sanction> sanctionsOf(String userId) {
        return sanctions.findByUserIdOrderByStartDateDesc(userId);
    }
}
