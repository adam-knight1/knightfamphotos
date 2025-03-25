package com.fam.knightfam.service;



import com.fam.knightfam.entity.User;
import com.fam.knightfam.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        // Encode the password for security
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Saving user with name: {}", user.getName());
        return userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));
    }
}

/*
    public UserDto getUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with ID: " + userId));
        return new UserDto(user.getUserId(), user.getName(), user.getEmail(), user.getProfilePictureUrl());
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
*/
