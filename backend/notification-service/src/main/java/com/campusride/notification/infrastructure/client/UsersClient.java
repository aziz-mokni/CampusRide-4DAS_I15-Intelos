package com.campusride.notification.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client du service Utilisateurs. Sans URL, le nom USERS est resolu par Eureka (lb://USERS).
 * Pour pointer directement un service local : clients.users.url=http://localhost:8081
 */
@FeignClient(name = "USERS", url = "${clients.users.url:}")
public interface UsersClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUser(@PathVariable("id") String id);
}
