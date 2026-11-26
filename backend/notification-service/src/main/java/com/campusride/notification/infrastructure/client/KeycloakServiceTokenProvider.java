package com.campusride.notification.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

/**
 * Obtient aupres de Keycloak un jeton "service" (client credentials) pour appeler
 * les autres microservices quand il n'y a pas de jeton utilisateur (traitement d'un evenement).
 */
@Component
public class KeycloakServiceTokenProvider {

    private final RestClient restClient = RestClient.create();
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;

    private String token;
    private Instant expiresAt = Instant.EPOCH;

    public KeycloakServiceTokenProvider(@Value("${keycloak.token-uri}") String tokenUri,
                                        @Value("${keycloak.client-id}") String clientId,
                                        @Value("${keycloak.client-secret}") String clientSecret) {
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public synchronized String getToken() {
        if (token != null && Instant.now().isBefore(expiresAt.minusSeconds(15))) {
            return token;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        Map<String, Object> response = restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (response == null || response.get("access_token") == null) {
            throw new IllegalStateException("Keycloak did not return an access token");
        }
        token = String.valueOf(response.get("access_token"));
        int expiresIn = response.get("expires_in") instanceof Number n ? n.intValue() : 60;
        expiresAt = Instant.now().plusSeconds(expiresIn);
        return token;
    }
}
