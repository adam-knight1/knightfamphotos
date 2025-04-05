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

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private String clientId = "1eddhu1oale604stl9e348bq0i";
    private String cognitoDomain = "https://us-east-24l2pj9fxk.auth.us-east-2.amazoncognito.com";
    private String logoutRedirectUrl = "http://localhost:8080/";

    // Removed the cognitoSecrets dependency
    public SecurityConfiguration(Environment env) {
        // Provide a fallback if necessary
        this.clientId = "1eddhu1oale604stl9e348bq0i";  // can also load this from env if desired.
        this.cognitoDomain = "https://us-east-24l2pj9fxk.auth.us-east-2.amazoncognito.com";
        this.logoutRedirectUrl = "http://localhost:8080/";
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CognitoLogoutHandler logoutHandler = new CognitoLogoutHandler(
                this.cognitoDomain,
                this.logoutRedirectUrl
        );

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/error/**", "/oauth2/**", "/api/photos").permitAll()
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

   //below is the suggested template from cognito docs

       /* @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            CognitoLogoutHandler cognitoLogoutHandler = new CognitoLogoutHandler();

            http.csrf(Customizer.withDefaults())
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/").permitAll()
                            .anyRequest()
                            .authenticated())
                    .oauth2Login(Customizer.withDefaults())
                    .logout(logout -> logout.logoutSuccessHandler(cognitoLogoutHandler));
            return http.build();
        }
    }*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
