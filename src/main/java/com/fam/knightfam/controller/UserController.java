package com.fam.knightfam.controller;

import com.fam.knightfam.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaim("sub");
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");

        return new User(userId, email, name);
    }

    @GetMapping("/claims")
    public Map<String, Object> getClaims(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }
}
