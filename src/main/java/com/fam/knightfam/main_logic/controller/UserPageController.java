package com.fam.knightfam.main_logic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserPageController {

    @GetMapping("/user-page")
    public String userPage() {
        return "user-page"; // maps to user-page.html in src/main/resources/templates
    }
}

