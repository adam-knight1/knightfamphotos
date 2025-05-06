package com.fam.knightfam.main_logic.controller;

import com.fam.knightfam.main_logic.entity.User;
import com.fam.knightfam.main_logic.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    // Constructor injection (no need for @Autowired when there's only one constructor)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating user with email: {} and name: {}", user.getEmail(), user.getName());
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /*@GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId) {
        try {
            UserDto userDto = userService.getUserData(userId);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            log.error("Failed to fetch user data for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching user data");
        }
    }*/
}
