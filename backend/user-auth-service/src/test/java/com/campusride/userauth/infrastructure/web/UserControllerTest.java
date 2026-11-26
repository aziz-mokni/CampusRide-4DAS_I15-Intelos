package com.campusride.userauth.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RabbitTemplate rabbitTemplate;   // pas de broker pendant les tests

    private static final String BODY =
            "{\"firstName\":\"Nour\",\"lastName\":\"Nasfi\",\"phone\":\"22111222\","
            + "\"university\":\"ESPRIT\",\"bio\":\"test\"}";

    @Test
    void createUser_returns201() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .with(jwt().jwt(j -> j.subject("kc-1").claim("email", "nour@esprit.tn")))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.email").value("nour@esprit.tn"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void duplicateEmail_returns409() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .with(jwt().jwt(j -> j.subject("kc-dup-1").claim("email", "dup@esprit.tn")))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/users")
                        .with(jwt().jwt(j -> j.subject("kc-dup-2").claim("email", "dup@esprit.tn")))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void withoutToken_returns401() throws Exception {
        mvc.perform(get("/api/v1/users/abc")).andExpect(status().isUnauthorized());
    }

    @Test
    void unknownUser_returns404() throws Exception {
        mvc.perform(get("/api/v1/users/unknown").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void invalidBody_returns400() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .with(jwt().jwt(j -> j.subject("kc-x").claim("email", "x@esprit.tn")))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchStatus_requiresAdmin() throws Exception {
        String json = "{\"status\":\"BLOCKED\"}";
        mvc.perform(patch("/api/v1/users/unknown/status").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/v1/users/unknown/status")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }
}
