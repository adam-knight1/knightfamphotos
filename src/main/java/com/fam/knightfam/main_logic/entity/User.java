
package com.fam.knightfam.main_logic.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    public User() {}

    public User(UUID userId, String email, String name, String profilePictureUrl) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}

