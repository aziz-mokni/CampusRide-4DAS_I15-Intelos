package com.campusride.userauth.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class ProfileEntity {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String university;
    private String photoUrl;
    @Column(length = 1000)
    private String bio;

    protected ProfileEntity() {}

    public ProfileEntity(String id) { this.id = id; }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getUniversity() { return university; }
    public void setUniversity(String v) { this.university = v; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String v) { this.photoUrl = v; }
    public String getBio() { return bio; }
    public void setBio(String v) { this.bio = v; }
}