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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/photos")
public class PhotoWebController {
    private static final Logger log = LoggerFactory.getLogger(PhotoWebController.class);

    private final PhotoService photoService;
    private final UserService userService;

    public PhotoWebController(PhotoService photoService, UserService userService) {
        this.photoService = photoService;
        this.userService  = userService;
    }

    // Show gallery
    @GetMapping("/gallery")
    public String gallery(Model model) {
        List<Map<String,Object>> photos = photoService.getAllPhotosWithPresignedUrls();
        model.addAttribute("photos", photos);
        return "gallery";       // src/main/resources/templates/gallery.html
    }

    // Show upload form
    @GetMapping("/upload")
    public String showUploadForm() {
        return "photo";         // src/main/resources/templates/photo.html
    }

    // Handle form POST
    @PostMapping("/upload")
    public String handleUpload(
            @RequestParam("file")        MultipartFile file,
            @RequestParam("title")       String title,
            @RequestParam("description") String description,
            @AuthenticationPrincipal    OidcUser principal,
            RedirectAttributes          redirect
    ) {
        if (file.isEmpty()) {
            redirect.addFlashAttribute("error","Please choose a file.");
            return "redirect:/photos/upload";
        }

        try {
            String email = principal.getEmail();
            String name  = principal.getName();
            // create or fetch user
            User user = userService.getOrCreateUserFromCognito(email, name);
            photoService.uploadPhoto(file, title, description, user.getEmail());
            redirect.addFlashAttribute("success","Photo uploaded!");
            return "redirect:/photos/gallery";
        } catch (Exception e) {
            log.error("Upload failed", e);
            redirect.addFlashAttribute("error","Upload error: "+e.getMessage());
            return "redirect:/photos/upload";
        }
    }
}

