package com.fam.knightfam.photo_logic.controller;

import com.fam.knightfam.main_logic.entity.User;
import com.fam.knightfam.main_logic.service.UserService;
import com.fam.knightfam.photo_logic.service.PhotoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/photos") // note: different base path from /api/photos
public class PhotoWebController {
    private final PhotoService photoService;
    private final UserService userService;

    public PhotoWebController(PhotoService photoService, UserService userService) {
        this.photoService = photoService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @AuthenticationPrincipal OidcUser principal,
                              RedirectAttributes redirectAttributes) {

        String email = principal.getEmail();
        String name = principal.getName();

        User user = userService.getOrCreateUserFromCognito(email, name);

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File must not be empty.");
            return "redirect:/upload-page"; // or wherever your upload form is
        }

        try {
            photoService.uploadPhoto(file, title, description, user.getEmail());
            redirectAttributes.addFlashAttribute("message", "Photo uploaded successfully!");
            return "redirect:/gallery";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading photo: " + e.getMessage());
            return "redirect:/upload-page"; // back to form with error
        }
    }
}

