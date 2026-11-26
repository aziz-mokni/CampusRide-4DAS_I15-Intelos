package com.campusride.notification.infrastructure.client;

import com.campusride.notification.domain.exception.ContactLookupException;
import com.campusride.notification.domain.model.UserContact;
import com.campusride.notification.domain.port.UserDirectory;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Appel synchrone a Utilisateurs protege par Resilience4j :
 * timeout + coupe-circuit (CircuitBreakerFactory) et nouvelle tentative limitee (2 essais).
 * 404 -> Optional.empty() (la notification passera a FAILED), service indisponible -> ContactLookupException.
 */
@Slf4j
@Component
public class UserDirectoryAdapter implements UserDirectory {

    private static final int MAX_ATTEMPTS = 2;

    private final UsersClient usersClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public UserDirectoryAdapter(UsersClient usersClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.usersClient = usersClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public Optional<UserContact> findContact(String userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("users");
        return circuitBreaker.run(
                () -> callWithRetry(userId),
                throwable -> {
                    log.warn("Users service unavailable for user {}: {}", userId, throwable.toString());
                    throw new ContactLookupException("Users service unavailable", throwable);
                });
    }

    private Optional<UserContact> callWithRetry(String userId) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                UserResponse user = usersClient.getUser(userId);
                if (user == null || user.email() == null) {
                    return Optional.empty();
                }
                return Optional.of(new UserContact(user.email(), user.firstName()));
            } catch (FeignException.NotFound e) {
                return Optional.empty();           // 404 : pas une panne, pas de nouvelle tentative
            } catch (RuntimeException e) {
                last = e;
                log.debug("Attempt {}/{} failed for user {}: {}", attempt, MAX_ATTEMPTS, userId, e.toString());
                if (attempt < MAX_ATTEMPTS) {
                    try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                }
            }
        }
        throw last != null ? last : new IllegalStateException("User lookup failed");
    }
}
