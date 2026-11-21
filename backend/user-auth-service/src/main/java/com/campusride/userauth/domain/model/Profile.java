package com.campusride.userauth.domain.model;

public class Profile {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String university;
    private String photoUrl;
    private String bio;

    public Profile(String id, String firstName, String lastName, String phone,
                   String university, String photoUrl, String bio) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.university = university;
        this.photoUrl = photoUrl;
        this.bio = bio;
    }

    public void update(String firstName, String lastName, String phone,
                       String university, String photoUrl, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.university = university;
        this.photoUrl = photoUrl;
        this.bio = bio;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getUniversity() { return university; }
    public String getPhotoUrl() { return photoUrl; }
    public String getBio() { return bio; }
}
