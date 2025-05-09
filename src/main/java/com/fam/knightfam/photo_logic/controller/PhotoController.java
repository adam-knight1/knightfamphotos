package com.fam.knightfam.photo_logic.controller;

import com.fam.knightfam.main_logic.entity.User;
import com.fam.knightfam.main_logic.service.UserService;
import com.fam.knightfam.photo_logic.entity.Photo;
import com.fam.knightfam.photo_logic.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                                         @RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @AuthenticationPrincipal OidcUser principal) {

        String email = principal.getEmail(); // or principal.getAttribute("email")
        String name = principal.getName();
        log.info("Received file: {}", file.getOriginalFilename());
        log.info("Title: {}, Description: {}", title, description);
        log.info("Authenticated user email: {}", email);

        User user = userService.getOrCreateUserFromCognito(email, name);


        log.info("Received file: {}", file.getOriginalFilename());
        log.info("Title: {}, Description: {}", title, description);
        log.info("Authenticated Cognito user: {}", email);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File must not be empty.");
        }

        try {
            String photoUrl = photoService.uploadPhoto(file, title, description, user.getEmail());
            return ResponseEntity.ok(Map.of("url", photoUrl));
        } catch (Exception e) {
            log.error("Error uploading photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    //this is to return an html view so it works with thymeleaf.
    @GetMapping("/photo")
    public String photoUploadForm() {
        return "photo"; // → Returns a Thymeleaf HTML page
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

    /*@GetMapping("/get")
    public ResponseEntity<List<Map<String, Object>>> getAllPhotos() {
        try {
            List<Photo> photos = photoService.getAllPhotos();
            List<Map<String, Object>> response = photos.stream().map(photo -> {
                Map<String, Object> map = new HashMap<>();
                map.put("title", photo.getTitle());
                map.put("description", photo.getDescription());
                map.put("uploadTime", photo.getUploadTime());
                map.put("url", photoService.generatePresignedUrl(photo.getS3ObjectKey())); //replacing with signed URL
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }*/

    @GetMapping("/get")
    public ResponseEntity<List<Map<String, Object>>> getAllPhotos() {
        try {
            return ResponseEntity.ok(photoService.getAllPhotosWithPresignedUrls());
        } catch (Exception e) {
            log.error("Failed to retrieve photos", e);
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
