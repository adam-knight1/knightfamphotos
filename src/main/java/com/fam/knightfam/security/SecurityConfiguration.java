package com.fam.knightfam.security;

import com.fam.knightfam.auth.CognitoLogoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final String clientId;
    private final String cognitoDomain;
    private final String logoutRedirectUrl;

    public SecurityConfiguration(Environment env) {
        this.clientId = "1eddhu1oale604stl9e348bq0i";
        this.cognitoDomain = "https://us-east-2_4l2pj9fxk.auth.us-east-2.amazoncognito.com";
        this.logoutRedirectUrl = "https://knightfam.com";
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // instantiate your existing handler with the Cognito domain
        CognitoLogoutHandler logoutHandler = new CognitoLogoutHandler(cognitoDomain);

        http
                // disable CSRF so GET /logout works
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/error/**", "/oauth2/**").permitAll()
                        .requestMatchers(
                                "/api/photos/**",
                                "/vote/**",
                                "/user-page",
                                "/gallery.html",
                                "/voting.html",
                                "/createvote.html"
                        ).authenticated()
                        .anyRequest().authenticated()
                )

                .requestCache(cache -> cache.requestCache(new org.springframework.security.web.savedrequest.NullRequestCache()))

                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/user-page", true)
                )

                .logout(logout -> logout
                        // Treat GET /logout as the logout endpoint
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        // invoke your CognitoLogoutHandler
                        .logoutSuccessHandler(logoutHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
