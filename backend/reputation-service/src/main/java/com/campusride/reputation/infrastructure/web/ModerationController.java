package com.campusride.reputation.infrastructure.web;

import com.campusride.reputation.application.ModerationService;
import com.campusride.reputation.application.ModerationService.Decision;
import com.campusride.reputation.domain.model.Report;
import com.campusride.reputation.domain.model.ReportReason;
import com.campusride.reputation.domain.model.ReportStatus;
import com.campusride.reputation.domain.model.Sanction;
import com.campusride.reputation.domain.model.SanctionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
class ModerationController {

    // TODO(auth): reporterId / moderatorId will come from the JWT, and PATCH will require the ADMIN role
    record OpenReport(@NotBlank String reporterId, @NotBlank String reportedUserId, String tripId,
                      @NotNull ReportReason reason, @Size(max = 2000) String description) {}

    record DecideReport(@NotNull ReportStatus status, @NotBlank String moderatorId, @Valid SanctionRequest sanction) {}

    record SanctionRequest(@NotNull SanctionType type, @Size(max = 1000) String reason, Instant endDate) {}

    private final ModerationService moderation;

    ModerationController(ModerationService moderation) {
        this.moderation = moderation;
    }

    @PostMapping("/reports")
    ResponseEntity<Report> open(@Valid @RequestBody OpenReport body) {
        Report report = moderation.report(body.reporterId(), body.reportedUserId(), body.tripId(), body.reason(), body.description());
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(report.id()).toUri();
        return ResponseEntity.created(location).body(report);
    }

    @GetMapping("/reports")
    Page<Report> list(@RequestParam(required = false) ReportStatus status,
                      @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return moderation.list(status, pageable);
    }

    @GetMapping("/reports/{id}")
    Report get(@PathVariable String id) {
        return moderation.get(id);
    }

    @PatchMapping("/reports/{id}")
    Decision decide(@PathVariable String id, @Valid @RequestBody DecideReport body) {
        SanctionRequest s = body.sanction();
        return moderation.decide(id, body.status(), body.moderatorId(),
                s == null ? null : s.type(), s == null ? null : s.reason(), s == null ? null : s.endDate());
    }

    @DeleteMapping("/reports/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        moderation.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sanctions/{id}")
    Sanction sanction(@PathVariable String id) {
        return moderation.sanction(id);
    }

    @GetMapping("/users/{id}/sanctions")
    List<Sanction> sanctionsOf(@PathVariable String id) {
        return moderation.sanctionsOf(id);
    }
}
