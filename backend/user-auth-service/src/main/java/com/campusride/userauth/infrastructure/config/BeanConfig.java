package com.campusride.userauth.infrastructure.config;

import com.campusride.userauth.application.port.in.ManageUserUseCase;
import com.campusride.userauth.application.port.in.ManageVehicleUseCase;
import com.campusride.userauth.application.port.out.EventPublisherPort;
import com.campusride.userauth.application.port.out.UserRepositoryPort;
import com.campusride.userauth.application.service.UserService;
import com.campusride.userauth.application.service.VehicleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ManageUserUseCase manageUserUseCase(UserRepositoryPort users, EventPublisherPort events) {
        return new UserService(users, events);
    }

    @Bean
    public ManageVehicleUseCase manageVehicleUseCase(UserRepositoryPort users) {
        return new VehicleService(users);
    }
}