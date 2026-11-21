package com.campusride.userauth.infrastructure.persistence;

import com.campusride.userauth.domain.model.Role;
import com.campusride.userauth.domain.model.UserStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String keycloakId;
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleEntity> vehicles = new ArrayList<>();

    protected UserEntity() {}

    public UserEntity(String id) { this.id = id; }

    public String getId() { return id; }
    public String getKeycloakId() { return keycloakId; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public ProfileEntity getProfile() { return profile; }
    public void setProfile(ProfileEntity profile) { this.profile = profile; }
    public List<VehicleEntity> getVehicles() { return vehicles; }
}