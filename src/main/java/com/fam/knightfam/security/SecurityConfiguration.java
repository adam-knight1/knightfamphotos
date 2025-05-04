package com.fam.knightfam.security;

import com.fam.knightfam.auth.CognitoLogoutHandler;
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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final String clientId;
    private final String cognitoDomain;
    private final String logoutRedirectUrl;

    public SecurityConfiguration(Environment env) {
        // you can also pull these from env if you prefer
        this.clientId = "1eddhu1oale604stl9e348bq0i";
        this.cognitoDomain = "https://us-east-2_4l2pj9fxk.auth.us-east-2.amazoncognito.com";
        this.logoutRedirectUrl = "https://knightfam.com";
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CognitoLogoutHandler logoutHandler = new CognitoLogoutHandler(cognitoDomain);

        http
                // 1) Disable CSRF (so GET /logout works)
                .csrf(csrf -> csrf.disable())

                // 2) Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow actuator endpoints for health checks
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Public pages
                        .requestMatchers("/", "/login", "/error", "/error/**", "/oauth2/**").permitAll()

                        // Your protected API/UI paths
                        .requestMatchers(
                                "/api/photos/**",
                                "/vote/**",
                                "/user-page",
                                "/gallery.html",
                                "/voting.html",
                                "/createvote.html",
                                "/calendar.html",
                                "/create-event.html",
                                "/api/calendar/**",
                                "/api/events/**"
                        ).authenticated()

                        // Everything else requires auth
                        .anyRequest().authenticated()
                )

                // 3) Don’t trigger the default saved‐request cache
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))

                // 4) OAuth2 login
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/user-page", true)
                )

                // 5) Custom GET /logout handling
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
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
