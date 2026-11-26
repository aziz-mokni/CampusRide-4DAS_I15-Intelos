package com.campusride.reputation.infrastructure.web;

import com.campusride.reputation.application.CompletedTripService;
import com.campusride.reputation.domain.model.CompletedTrip;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.List;

/**
 * Temporary entry point to create completed trips by hand.
 * TODO(events): replace POST with a consumer of trip.completed once RabbitMQ is wired.
 */
@RestController
@RequestMapping("/api/v1/completed-trips")
class CompletedTripController {

    record RecordTrip(@NotBlank String tripId, @NotBlank String driverId,
                      @NotEmpty List<@NotBlank String> passengerIds, @NotNull Instant completedAt) {}

    private final CompletedTripService trips;

    CompletedTripController(CompletedTripService trips) {
        this.trips = trips;
    }

    @PostMapping
    ResponseEntity<CompletedTrip> record(@Valid @RequestBody RecordTrip body) {
        CompletedTrip trip = trips.record(body.tripId(), body.driverId(), body.passengerIds(), body.completedAt());
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{tripId}").buildAndExpand(trip.tripId()).toUri();
        return ResponseEntity.created(location).body(trip);
    }

    @GetMapping("/{tripId}")
    CompletedTrip get(@PathVariable String tripId) {
        return trips.get(tripId);
    }

    @DeleteMapping("/{tripId}")
    ResponseEntity<Void> delete(@PathVariable String tripId) {
        trips.delete(tripId);
        return ResponseEntity.noContent().build();
    }
}
