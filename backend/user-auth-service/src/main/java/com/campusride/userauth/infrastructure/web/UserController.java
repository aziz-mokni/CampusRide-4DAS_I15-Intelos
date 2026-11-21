package com.campusride.userauth.infrastructure.web;

import com.campusride.userauth.application.port.in.*;
import com.campusride.userauth.domain.model.User;
import com.campusride.userauth.domain.model.Vehicle;
import com.campusride.userauth.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final ManageUserUseCase userUseCase;
    private final ManageVehicleUseCase vehicleUseCase;

    public UserController(ManageUserUseCase userUseCase, ManageVehicleUseCase vehicleUseCase) {
        this.userUseCase = userUseCase;
        this.vehicleUseCase = vehicleUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest r,
                                               @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Le JWT ne contient pas d'adresse e-mail");
        }
        User user = userUseCase.create(new CreateUserCommand(jwt.getSubject(), email,
                r.firstName(), r.lastName(), r.phone(), r.university(), r.photoUrl(), r.bio()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(WebMapper.toResponse(user));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable String id) {
        return WebMapper.toResponse(userUseCase.getById(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable String id,
                               @Valid @RequestBody UpdateProfileRequest r,
                               Authentication auth, @AuthenticationPrincipal Jwt jwt) {
        checkOwnerOrAdmin(id, auth, jwt);
        return WebMapper.toResponse(userUseCase.updateProfile(id,
                new UpdateProfileCommand(r.firstName(), r.lastName(), r.phone(),
                        r.university(), r.photoUrl(), r.bio())));
    }

    @PostMapping("/{id}/vehicles")
    public ResponseEntity<VehicleResponse> addVehicle(@PathVariable String id,
                                                      @Valid @RequestBody AddVehicleRequest r,
                                                      Authentication auth,
                                                      @AuthenticationPrincipal Jwt jwt) {
        checkOwnerOrAdmin(id, auth, jwt);
        Vehicle v = vehicleUseCase.addVehicle(id,
                new AddVehicleCommand(r.brand(), r.model(), r.color(), r.plateNumber(), r.seats()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{vid}").buildAndExpand(v.getId()).toUri();
        return ResponseEntity.created(location).body(WebMapper.toResponse(v));
    }

    @GetMapping("/{id}/vehicles")
    public List<VehicleResponse> vehicles(@PathVariable String id) {
        return vehicleUseCase.listVehicles(id).stream().map(WebMapper::toResponse).toList();
    }

    @PatchMapping("/{id}/status")
    public UserResponse changeStatus(@PathVariable String id,
                                     @Valid @RequestBody ChangeStatusRequest r) {
        return WebMapper.toResponse(userUseCase.changeStatus(id, r.status()));
    }

    private void checkOwnerOrAdmin(String userId, Authentication auth, Jwt jwt) {
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (admin) {
            return;
        }
        User target = userUseCase.getById(userId);   // 404 si inconnu
        if (!target.getKeycloakId().equals(jwt.getSubject())) {
            throw new AccessDeniedException("Vous ne pouvez modifier que votre propre compte");
        }
    }
}
