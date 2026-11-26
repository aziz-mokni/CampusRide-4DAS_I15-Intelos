package com.campusride.notification.presentation.controller;

import com.campusride.notification.application.usecase.*;
import com.campusride.notification.domain.enums.Channel;
import com.campusride.notification.domain.enums.NotificationStatus;
import com.campusride.notification.domain.model.Notification;
import com.campusride.notification.presentation.dto.CreateNotificationRequest;
import com.campusride.notification.presentation.dto.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ListNotificationsUseCase listNotifications;
    private final GetNotificationUseCase getNotification;
    private final MarkNotificationAsReadUseCase markNotificationAsRead;
    private final DeleteNotificationUseCase deleteNotification;
    private final CreateNotificationUseCase createNotification;

    @GetMapping
    @Operation(summary = "Notifications de l'utilisateur connecte (200, 401)",
               description = "Filtre optionnel par statut ; canal IN_APP par defaut (channel=EMAIL pour voir les e-mails).")
    public List<NotificationResponse> list(@AuthenticationPrincipal Jwt jwt,
                                           @RequestParam(required = false) NotificationStatus status,
                                           @RequestParam(required = false) Channel channel,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Channel effectiveChannel = channel == null ? Channel.IN_APP : channel;
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return listNotifications.execute(jwt.getSubject(), status, effectiveChannel, safePage, safeSize)
                .stream().map(NotificationResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une notification (200, 401, 403, 404)")
    public NotificationResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return NotificationResponse.from(getNotification.execute(id, jwt.getSubject()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Marquer comme lue (200, 401, 403, 404)")
    public NotificationResponse markAsRead(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return NotificationResponse.from(markNotificationAsRead.execute(id, jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification (204, 401, 403, 404)")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        deleteNotification.execute(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Creer une notification manuellement - ADMIN (201, 400, 401, 403)")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody CreateNotificationRequest request) {
        Notification created = createNotification.execute(request.userId(), request.type(), request.message());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(NotificationResponse.from(created));
    }
}
