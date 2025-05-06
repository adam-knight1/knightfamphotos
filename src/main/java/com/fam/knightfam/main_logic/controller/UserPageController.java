package com.fam.knightfam.main_logic.controller;

import com.fam.knightfam.main_logic.service.UserService;
import com.fam.knightfam.main_logic.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    private static final Logger log = LoggerFactory.getLogger(UserPageController.class);
    private final UserService userService;

    public UserPageController(UserService userService) {
        this.userService = userService;
    }

   /* @GetMapping("/user-page")
    public String userPage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        String email = oidcUser.getEmail();
        model.addAttribute("displayName", email);
        model.addAttribute("profilePicUrl", userService
                .findUserByEmail(email)
                .getProfilePictureUrl());
        return "user-page";
    }*/

    //or.... using cognito to fetch name using OIDC

    @GetMapping("/user-page")
    public String userPage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        String email = oidcUser.getEmail();
        // this is the OIDC “name” claim, if you’ve set it up in Cognito
        String friendlyName = oidcUser.getAttribute("name");
        model.addAttribute("displayName",
                friendlyName != null ? friendlyName : email);
        model.addAttribute("profilePicUrl", userService
                .findUserByEmail(email)
                .getProfilePictureUrl());
        return "user-page";
    }


}
