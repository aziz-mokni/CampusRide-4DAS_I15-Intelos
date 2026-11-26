package com.campusride.notification.presentation.controller;

import com.campusride.notification.application.usecase.*;
import com.campusride.notification.domain.model.Conversation;
import com.campusride.notification.domain.model.Message;
import com.campusride.notification.presentation.dto.*;
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
@RequestMapping("/api/v1/conversations")
@Tag(name = "Messagerie")
@RequiredArgsConstructor
public class ConversationController {

    private final CreateConversationUseCase createConversation;
    private final ListConversationsUseCase listConversations;
    private final GetMessagesUseCase getMessages;
    private final SendMessageUseCase sendMessage;

    @PostMapping
    @Operation(summary = "Creer une conversation (201, 400, 401)")
    public ResponseEntity<ConversationResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                       @Valid @RequestBody CreateConversationRequest request) {
        Conversation c = createConversation.execute(jwt.getSubject(), request.tripId(), request.participantIds());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}/messages").buildAndExpand(c.getId()).toUri();
        return ResponseEntity.created(location).body(ConversationResponse.from(c));
    }

    @GetMapping
    @Operation(summary = "Mes conversations (200, 401)")
    public List<ConversationResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return listConversations.execute(jwt.getSubject()).stream().map(ConversationResponse::from).toList();
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Historique du chat (200, 401, 403, 404)")
    public List<MessageResponse> messages(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return getMessages.execute(id, jwt.getSubject()).stream().map(MessageResponse::from).toList();
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Envoyer un message (201 + Location, 400, 401, 403, 404)")
    public ResponseEntity<MessageResponse> send(@AuthenticationPrincipal Jwt jwt, @PathVariable String id,
                                                @Valid @RequestBody SendMessageRequest request) {
        Message m = sendMessage.execute(id, jwt.getSubject(), request.text());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{messageId}").buildAndExpand(m.getId()).toUri();
        return ResponseEntity.created(location).body(MessageResponse.from(m));
    }
}
