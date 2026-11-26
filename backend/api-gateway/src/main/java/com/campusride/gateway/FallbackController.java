package com.campusride.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/** Reponse de repli quand le coupe-circuit de la Gateway est ouvert (service en panne). */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "title", "Service Unavailable",
                "status", 503,
                "detail", "Le service Notifications est temporairement indisponible",
                "degraded", true)));
    }
}
