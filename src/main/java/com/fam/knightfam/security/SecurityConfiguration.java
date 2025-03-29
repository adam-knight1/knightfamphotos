package com.fam.knightfam.security;

import com.fam.knightfam.auth.CognitoLogoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;


/*Security Configuration class for spring security, it allows or restricts access to various endpoints. */
/*THere is also a bcrypt password encoder, which may or may not be necessary in production, but does serve to pass tests at the moment*/
@Configuration
public class SecurityConfiguration {

    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String clientId;

    @Value("${cognito.domain}")
    private String cognitoDomain;

    @Value("${cognito.logout-redirect-url}")
    private String logoutRedirectUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
    public PasswordEncoder passwordEncoder() { //added to pass initial tests, may not need
        return new BCryptPasswordEncoder();
    }
}
