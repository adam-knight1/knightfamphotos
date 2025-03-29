package com.fam.knightfam.controller;

import com.fam.knightfam.entity.Photo;
import com.fam.knightfam.entity.User;
import com.fam.knightfam.service.PhotoService;
import com.fam.knightfam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/*Photo service contains business logic for photo actions such as uploading, deleting photos.  Interacts with Photo Controller and repo
* Photos are uploaded to S3 bucket, while metadata is uploaded to RDS and references the corresponding bucket*/
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

    public PhotoController(PhotoService photoService, UserService userService) {
        this.photoService = photoService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal Jwt jwt) {
        // Extract email from the JWT claims
        String email = jwt.getClaimAsString("email");
        log.info("Received file: {}", file.getOriginalFilename());
        log.info("Authenticated user email: {}", email);

        if (file == null || file.isEmpty()) {
            log.error("File must not be empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must not be empty.");
        }

        if (email == null) {
            log.error("No authenticated user email found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user found.");
        }

        try {
            String photoUrl = photoService.uploadPhoto(file, email);
            log.info("Photo uploaded successfully: {}", photoUrl);
            return ResponseEntity.ok(Map.of("url", photoUrl));
        } catch (Exception e) {
            log.error("Error uploading photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/upload/profile")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                  @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        try {
            String photoUrl = photoService.uploadProfilePicture(file, email);
            return ResponseEntity.ok(Map.of("url", photoUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<Photo>> getAllPhotos() {
        try {
            List<Photo> photos = photoService.getAllPhotos();
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfilePhoto(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        try {
            if (email == null) {
                log.error("No authenticated user email found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user found.");
            }
            User user = userService.findUserByEmail(email);
            if (user == null) {
                log.error("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            String profilePhotoUrl = user.getProfilePictureUrl();
            if (profilePhotoUrl == null) {
                log.error("Profile photo not found for user: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile photo not found");
            }
            log.info("Profile photo URL: {}", profilePhotoUrl);
            return ResponseEntity.ok(Map.of("url", profilePhotoUrl));
        } catch (Exception e) {
            log.error("Error retrieving profile photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
