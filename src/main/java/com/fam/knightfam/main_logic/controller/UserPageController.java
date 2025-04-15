package com.fam.knightfam.main_logic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserPageController {

    private static final Logger log = LoggerFactory.getLogger(UserPageController.class);


    @GetMapping("/user-page")
    public String userPage(@AuthenticationPrincipal OidcUser user) {
        log.info("Logged in as: {}", user.getEmail());
        log.info("Authorities: {}", user.getAuthorities());
        return "user-page";
    }
}

