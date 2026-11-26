package com.campusride.reputation.infrastructure.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Server url "/" is relative to the page showing the docs: "Try it out" goes through the gateway
 * when the docs are opened from the gateway portal (:8080), and to the service when opened on :8084.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "CampusRide - Reputation Service", version = "v1",
                description = "Avis, score de confiance, signalements et sanctions"),
        servers = @Server(url = "/"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
class OpenApiConfig {
}
