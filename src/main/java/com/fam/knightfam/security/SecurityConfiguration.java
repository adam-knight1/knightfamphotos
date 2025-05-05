package com.fam.knightfam.security;

import com.fam.knightfam.auth.CognitoLogoutHandler;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.filter.ForwardedHeaderFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${cognito.domain}")
    private String cognitoDomain;

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CognitoLogoutHandler logoutHandler = new CognitoLogoutHandler(cognitoDomain);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/", "/login", "/error", "/oauth2/**").permitAll()
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
                        .anyRequest().authenticated()
                )
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/user-page", true)
                )
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
    public CognitoLogoutHandler logoutHandler() {
        return new CognitoLogoutHandler(cognitoDomain);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
