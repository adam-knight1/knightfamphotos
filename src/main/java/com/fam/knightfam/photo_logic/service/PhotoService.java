package com.fam.knightfam.photo_logic.service;

import com.fam.knightfam.main_logic.entity.User;
import com.fam.knightfam.main_logic.service.UserService;
import com.fam.knightfam.photo_logic.entity.Photo;
import com.fam.knightfam.photo_logic.repository.PhotoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PhotoService {
    private final S3Client s3Client;
    private final SqsClient sqsClient;
    private final PhotoRepository photoRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private final String bucketName;
    private final String region;
    private final String queueUrl; // SQS queue URL
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PhotoService(Environment env,
                        PhotoRepository photoRepository,
                        UserService userService) {

        this.bucketName = env.getProperty("aws.s3.bucket", "placeholder-bucket");
        this.region = env.getProperty("aws.s3.region", "us-east-2");
        this.queueUrl = env.getProperty("aws.sqs.queueUrl", "https://sqs.us-east-2.amazonaws.com/your-account-id/your-queue-name");

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("AWS S3 region cannot be null or blank");
        }

        this.photoRepository = photoRepository;
        this.userService = userService;

        this.s3Client = S3Client.builder()
                .region(Region.of(this.region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        this.sqsClient = SqsClient.builder()
                .region(Region.of(this.region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String uploadPhoto(MultipartFile file, String title, String description, String email) throws IOException {
        log.info("Reached service");
        String s3Key = generateUniqueKey(file.getOriginalFilename(), email);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .acl("public-read")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String photoUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);

        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Photo photo = new Photo();
        photo.setTitle(title); //testing testing 123
        photo.setDescription(description);
        photo.setUrl(photoUrl);
        photo.setS3ObjectKey(s3Key);
        photo.setUser(user);
        photo.setUploadTime(LocalDateTime.now());

        photoRepository.save(photo);

        // Publish an event to SQS for additional processing (e.g., thumbnail generation)
        publishPhotoUploadEvent(s3Key, photoUrl, email);

        return photoUrl;
    }

    public String uploadProfilePicture(MultipartFile file, String email) throws IOException {
        log.info("Starting uploadProfilePicture method");
        String s3Key = generateUniqueKey(file.getOriginalFilename(), email);
        log.info("Generated S3 key: {}", s3Key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .acl("public-read")
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new IOException("Error uploading file to S3", e);
        }

        String profilePicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
        log.info("Constructed profile picture URL: {}", profilePicUrl);

        try {
            log.info("Fetching user by email: {}", email);
            User user = userService.findUserByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found for email: " + email);
            }
            user.setProfilePictureUrl(profilePicUrl);
            userService.updateUser(user);
            log.info("User profile picture URL updated");
        } catch (UsernameNotFoundException e) {
            log.error("User not found for email: {}", email, e);
            throw new IOException("User not found", e);
        } catch (DataAccessException e) {
            log.error("Database access error", e);
            throw new IOException("Error updating user information", e);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw e;
        }

        log.info("Completed uploadProfilePicture method");
        return profilePicUrl;
    }

    private void publishPhotoUploadEvent(String s3Key, String photoUrl, String email) {
        try {
            // Create a simple event map; alternatively, create a dedicated POJO (e.g., PhotoUploadEvent)
            Map<String, String> event = new HashMap<>();
            event.put("s3Key", s3Key);
            event.put("photoUrl", photoUrl);
            event.put("email", email);
            event.put("timestamp", LocalDateTime.now().toString());

            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            log.info("Published photo upload event to SQS for s3Key: {}", s3Key);
        } catch (Exception e) {
            log.error("Failed to publish photo upload event to SQS", e);
            // Depending on your design, you may want to retry or handle this failure differently
        }
    }

    private String generateUniqueKey(String originalFilename, String username) {
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String baseName = StringUtils.stripFilenameExtension(originalFilename);
        return String.format("%s_%s_%s.%s", username, baseName, UUID.randomUUID(), fileExtension);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo getProfilePhoto(String email) {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        Photo profilePhoto = new Photo();
        profilePhoto.setUrl(user.getProfilePictureUrl());
        return profilePhoto;
    }
}
