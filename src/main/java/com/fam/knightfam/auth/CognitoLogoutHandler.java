package com.fam.knightfam.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CognitoLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private final String domain;
    private final String clientId;

    public CognitoLogoutHandler(String domain) {
        Map<String, String> secrets = fetchSecrets();
        this.domain = domain;
        this.clientId = secrets.getOrDefault("clientId", "1eddhu1oale604stl9e348bq0i");
    }

    private static Map<String, String> fetchSecrets() {
        String secretName = "Cognito-user-data";
        Region region = Region.of("us-east-2");

        SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

        GetSecretValueResponse response = client.getSecretValue(
                GetSecretValueRequest.builder().secretId(secretName).build());

        try {
            return new ObjectMapper().readValue(response.secretString(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse secret JSON", e);
        }
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String host = request.getServerName();

        String redirectUrl;
        if (host.contains("localhost")) {
            redirectUrl = "http://localhost:8080/";
        } else {
            redirectUrl = "https://knightfam.com/";
        }

        return UriComponentsBuilder
                .fromHttpUrl(domain + "/logout")
                .queryParam("client_id", clientId)
                .queryParam("logout_uri", redirectUrl)
                .build()
                .toUriString();
    }
}
