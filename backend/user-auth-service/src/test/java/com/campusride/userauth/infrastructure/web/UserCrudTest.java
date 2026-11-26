package com.campusride.userauth.infrastructure.web;

import com.jayway.jsonpath.JsonPath;
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
class UserCrudTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RabbitTemplate rabbitTemplate;   // pas de broker pendant les tests

    private static final String USER =
            "{\"firstName\":\"Nour\",\"lastName\":\"Nasfi\",\"university\":\"ESPRIT\"}";

    private static String vehicle(String plate, int seats) {
        return "{\"brand\":\"Renault\",\"model\":\"Clio\",\"color\":\"rouge\","
                + "\"plateNumber\":\"" + plate + "\",\"seats\":" + seats + "}";
    }

    private String createUser(String subject, String email) throws Exception {
        String body = mvc.perform(post("/api/v1/users")
                        .with(jwt().jwt(j -> j.subject(subject).claim("email", email)))
                        .contentType(MediaType.APPLICATION_JSON).content(USER))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    private String addVehicle(String userId, String subject, String plate) throws Exception {
        String body = mvc.perform(post("/api/v1/users/" + userId + "/vehicles")
                        .with(jwt().jwt(j -> j.subject(subject)))
                        .contentType(MediaType.APPLICATION_JSON).content(vehicle(plate, 4)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    @Test
    void vehicleCrud_fullCycle() throws Exception {
        String userId = createUser("kc-crud-1", "crud1@esprit.tn");
        String vehicleId = addVehicle(userId, "kc-crud-1", "100 TUN 1");

        mvc.perform(get("/api/v1/users/" + userId + "/vehicles").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(put("/api/v1/users/" + userId + "/vehicles/" + vehicleId)
                        .with(jwt().jwt(j -> j.subject("kc-crud-1")))
                        .contentType(MediaType.APPLICATION_JSON).content(vehicle("100 TUN 1", 5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats").value(5));

        mvc.perform(delete("/api/v1/users/" + userId + "/vehicles/" + vehicleId)
                        .with(jwt().jwt(j -> j.subject("kc-crud-1"))))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/users/" + userId + "/vehicles").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void me_returnsTheConnectedUser() throws Exception {
        createUser("kc-me", "me@esprit.tn");

        mvc.perform(get("/api/v1/users/me").with(jwt().jwt(j -> j.subject("kc-me"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me@esprit.tn"));
    }

    @Test
    void duplicatePlate_returns409() throws Exception {
        String a = createUser("kc-plate-a", "platea@esprit.tn");
        String b = createUser("kc-plate-b", "plateb@esprit.tn");
        addVehicle(a, "kc-plate-a", "200 TUN 2");

        mvc.perform(post("/api/v1/users/" + b + "/vehicles")
                        .with(jwt().jwt(j -> j.subject("kc-plate-b")))
                        .contentType(MediaType.APPLICATION_JSON).content(vehicle("200 TUN 2", 4)))
                .andExpect(status().isConflict());
    }

    @Test
    void notOwner_cannotDeleteVehicle() throws Exception {
        String owner = createUser("kc-owner", "owner@esprit.tn");
        String vehicleId = addVehicle(owner, "kc-owner", "300 TUN 3");

        mvc.perform(delete("/api/v1/users/" + owner + "/vehicles/" + vehicleId)
                        .with(jwt().jwt(j -> j.subject("kc-intruder"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUnknownVehicle_returns404() throws Exception {
        String userId = createUser("kc-nov", "nov@esprit.tn");

        mvc.perform(put("/api/v1/users/" + userId + "/vehicles/nope")
                        .with(jwt().jwt(j -> j.subject("kc-nov")))
                        .contentType(MediaType.APPLICATION_JSON).content(vehicle("400 TUN 4", 4)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listUsers_requiresAdmin() throws Exception {
        mvc.perform(get("/api/v1/users").with(jwt()))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/v1/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.content").isArray());
    }
}
