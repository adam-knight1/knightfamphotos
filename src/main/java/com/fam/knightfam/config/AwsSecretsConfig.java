package com.fam.knightfam.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;


import java.io.IOException;
import java.util.Map;

/* Class to interact with AWS secrets manager*/
@Configuration
public class AwsSecretsConfig {

    @Bean
    public Map<String, String> awsSecrets() {
        String secretName = "Cognito-user-data";
        Region region = Region.of("us-east-2");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);

        String secretJson = getSecretValueResponse.secretString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(secretJson, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse secrets JSON", e);
        }
    }
}
