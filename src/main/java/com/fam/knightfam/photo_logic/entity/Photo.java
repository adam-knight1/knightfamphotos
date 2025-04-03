package com.fam.knightfam.photo_logic.entity;

import com.fam.knightfam.main_logic.entity.User;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String s3ObjectKey;
    private String url; // URL to access the photo
    private LocalDateTime uploadTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user; // A user can have many photos

    public Photo() {}

    public Photo(String title, String description, String s3ObjectKey, String url, User user) {
        this.title = title;
        this.description = description;
        this.s3ObjectKey = s3ObjectKey;
        this.url = url;
        this.uploadTime = LocalDateTime.now();
        this.user = user;
    }

    // Getters and setters below
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getS3ObjectKey() {
        return s3ObjectKey;
    }
    public void setS3ObjectKey(String s3ObjectKey) {
        this.s3ObjectKey = s3ObjectKey;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
