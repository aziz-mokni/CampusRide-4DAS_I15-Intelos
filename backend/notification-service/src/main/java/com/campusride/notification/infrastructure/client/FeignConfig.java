package com.campusride.notification.infrastructure.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /** Ajoute le jeton Keycloak (client credentials) a chaque appel Feign. */
    @Bean
    public RequestInterceptor bearerTokenInterceptor(KeycloakServiceTokenProvider tokenProvider) {
        return template -> template.header("Authorization", "Bearer " + tokenProvider.getToken());
    }
}
