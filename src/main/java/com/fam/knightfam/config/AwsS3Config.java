package com.fam.knightfam.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/*S3 config class that defines S3 builder to allow uploads to S3 ex. photos with metadata uploaded to RDS Postgres */

@Configuration
public class AwsS3Config {

    @Value("${aws.s3.region:us-east-2}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
    //adding below to fix region env not populating
    @PostConstruct
    public void debugRegion() {
        System.out.println("⚙️ AWS S3 Region (Injected): " + region);
    }
}
