package com.fam.knightfam.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CognitoLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private final String domain;         // e.g., "https://your-cognito-domain.auth.region.amazoncognito.com"
    private final String clientId;       // Cognito App Client ID
    private final String logoutRedirectUrl;  // Where to redirect after logout

    public CognitoLogoutHandler(String domain, String clientId, String logoutRedirectUrl) {
        this.domain = domain;
        this.clientId = clientId;
        this.logoutRedirectUrl = logoutRedirectUrl;
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
