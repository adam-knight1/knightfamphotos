package com.fam.knightfam.security;

import com.fam.knightfam.auth.CognitoLogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, Environment env) throws Exception {
        String clientId = env.getProperty("spring.security.oauth2.client.registration.cognito.client-id", "placeholder");
        String cognitoDomain = env.getProperty("cognito.domain", "https://example.auth.us-east-2.amazoncognito.com");
        String logoutRedirectUrl = env.getProperty("cognito.logout-redirect-url", "http://localhost:8080/");

        CognitoLogoutHandler logoutHandler = new CognitoLogoutHandler(cognitoDomain, clientId, logoutRedirectUrl);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/error/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout.logoutSuccessHandler(logoutHandler));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

