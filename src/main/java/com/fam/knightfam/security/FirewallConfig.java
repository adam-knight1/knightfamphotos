package com.fam.knightfam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/* Using this to allow semicolons to stop getting malicious string error and not have to use cookies for sessions" */

@Configuration
public class FirewallConfig {

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // Allowing semicolons in the URL
        firewall.setAllowSemicolon(true);
        return firewall;
    }
}
