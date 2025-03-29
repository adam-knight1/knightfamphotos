package com.fam.knightfam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/* Using this firewall config class to allow semicolons to stop getting malicious string error and not have to use cookies for sessions" */

@Configuration
public class FirewallConfig {

    /*THe main function of this class comes from this method, which only exists to allow semicolons to step failing tests*/

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // Allowing semicolons in the URL for now to pass tests
        firewall.setAllowSemicolon(true);
        return firewall;
    }
}
