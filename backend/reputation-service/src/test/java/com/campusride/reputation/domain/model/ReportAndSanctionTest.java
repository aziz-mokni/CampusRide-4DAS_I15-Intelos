package com.campusride.reputation.domain.model;

import com.campusride.reputation.domain.exception.ConflictException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ReportAndSanctionTest {

    private final Report pending = Report.open("reporter", "reported", "t1", ReportReason.NO_SHOW, "never came");

    @Test
    void newReportIsPendingAndCannotTargetItsAuthor() {
        assertEquals(ReportStatus.PENDING, pending.status());
        assertThrows(IllegalArgumentException.class, () -> Report.open("me", "me", null, ReportReason.OTHER, null));
    }

    @Test
    void followsItsLifecycleAndDecisionsAreFinal() {
        Report underReview = pending.moveTo(ReportStatus.UNDER_REVIEW, "admin");
        assertNull(underReview.decidedAt());
        assertThrows(IllegalArgumentException.class, () -> underReview.moveTo(ReportStatus.PENDING, "admin"));

        Report accepted = underReview.moveTo(ReportStatus.ACCEPTED, "admin");
        assertNotNull(accepted.decidedAt());
        assertEquals("admin", accepted.moderatorId());
        assertThrows(ConflictException.class, () -> accepted.moveTo(ReportStatus.REJECTED, "admin"));
    }

    @Test
    void sanctionRequiresAnAcceptedReport() {
        assertThrows(ConflictException.class, () -> Sanction.apply(pending, SanctionType.WARNING, "x", null));
        Report rejected = pending.moveTo(ReportStatus.REJECTED, "admin");
        assertThrows(ConflictException.class, () -> Sanction.apply(rejected, SanctionType.WARNING, "x", null));
    }

    @Test
    void suspensionNeedsAFutureEndDateOthersHaveNone() {
        Report accepted = pending.moveTo(ReportStatus.ACCEPTED, "admin");
        Instant tomorrow = Instant.now().plus(1, ChronoUnit.DAYS);

        assertThrows(IllegalArgumentException.class, () -> Sanction.apply(accepted, SanctionType.SUSPENSION, "x", null));
        assertThrows(IllegalArgumentException.class,
                () -> Sanction.apply(accepted, SanctionType.SUSPENSION, "x", Instant.now().minusSeconds(60)));

        Sanction suspension = Sanction.apply(accepted, SanctionType.SUSPENSION, "x", tomorrow);
        assertEquals("reported", suspension.userId());
        assertEquals(accepted.id(), suspension.reportId());
        assertEquals(tomorrow, suspension.endDate());

        assertNull(Sanction.apply(accepted, SanctionType.BAN, "x", tomorrow).endDate());
        assertNull(Sanction.apply(accepted, SanctionType.WARNING, "x", tomorrow).endDate());
    }
}
