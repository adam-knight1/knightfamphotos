package com.fam.knightfam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*basic login controller for temporary dev use, login maintained by cognito*/
/* will expand when react frontend implemented */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
