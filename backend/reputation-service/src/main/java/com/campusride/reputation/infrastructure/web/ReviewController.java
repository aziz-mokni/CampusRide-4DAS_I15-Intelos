package com.campusride.reputation.infrastructure.web;

import com.campusride.reputation.application.ReviewService;
import com.campusride.reputation.domain.model.Review;
import com.campusride.reputation.domain.model.Score;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
class ReviewController {

    // TODO(auth): authorId will come from the JWT once Keycloak is wired in the gateway
    record PublishReview(@NotBlank String tripId, @NotBlank String authorId, @NotBlank String targetId,
                         @Min(1) @Max(5) int rating, @Size(max = 1000) String comment) {}

    record EditReview(@Min(1) @Max(5) int rating, @Size(max = 1000) String comment) {}

    private final ReviewService reviews;

    ReviewController(ReviewService reviews) {
        this.reviews = reviews;
    }

    @PostMapping("/reviews")
    ResponseEntity<Review> publish(@Valid @RequestBody PublishReview body) {
        Review review = reviews.publish(body.tripId(), body.authorId(), body.targetId(), body.rating(), body.comment());
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(review.id()).toUri();
        return ResponseEntity.created(location).body(review);
    }

    @GetMapping("/reviews/{id}")
    Review get(@PathVariable String id) {
        return reviews.get(id);
    }

    @PutMapping("/reviews/{id}")
    Review edit(@PathVariable String id, @Valid @RequestBody EditReview body) {
        return reviews.edit(id, body.rating(), body.comment());
    }

    @DeleteMapping("/reviews/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        reviews.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}/reviews")
    Page<Review> receivedBy(@PathVariable String id,
                            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviews.receivedBy(id, pageable);
    }

    @GetMapping("/users/{id}/score")
    Score score(@PathVariable String id) {
        return reviews.scoreOf(id);
    }
}
