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
    private final String logoutRedirectUrl;

    public CognitoLogoutHandler(String domain, String logoutRedirectUrl) {
        Map<String, String> secrets = fetchSecrets(); // Use utility method

        this.domain = domain;
        this.clientId = secrets.get("clientId");
        this.logoutRedirectUrl = logoutRedirectUrl;
    }

    private static Map<String, String> fetchSecrets() {
        String secretName = "Cognito-user-data";
        Region region = Region.of("us-east-2");

        SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);

        String json = response.secretString();

        // Convert JSON to Map
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse secret JSON", e);
        }
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return UriComponentsBuilder
                .fromUri(URI.create(domain + "/logout"))
                .queryParam("client_id", clientId)
                .queryParam("logout_uri", logoutRedirectUrl)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}

