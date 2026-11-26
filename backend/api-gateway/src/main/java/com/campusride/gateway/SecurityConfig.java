package com.campusride.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * La Gateway valide la signature et l'expiration du JWT emis par Keycloak (cles publiques JWK),
 * avant de router la requete. gateway.security.enabled=false desactive le controle (tests de routage).
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            @Value("${gateway.security.enabled:true}") boolean securityEnabled) {

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        if (!securityEnabled) {
            return http.authorizeExchange(exchange -> exchange.anyExchange().permitAll()).build();
        }

        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/health/**", "/actuator/info",
                                      "/swagger-ui.html", "/swagger-ui/**", "/webjars/**",
                                      "/v3/api-docs/**", "/docs/**", "/fallback/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();
    }
}
