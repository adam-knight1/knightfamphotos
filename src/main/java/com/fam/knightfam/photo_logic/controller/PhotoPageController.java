package com.fam.knightfam.photo_logic.controller;

import com.fam.knightfam.photo_logic.entity.Photo;
import com.fam.knightfam.photo_logic.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//this differs from the photo logic controller, this is mainly concerned with
//how photos are viewed rather than how they are processed, I wanted some separation of concern.
@Controller
public class PhotoPageController {
    private final PhotoService photoService;

    public PhotoPageController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/gallery")
    public String galleryPage(Model model) {
        List<Photo> photos = photoService.getAllPhotos();
        model.addAttribute("photos", photos);
        return "gallery";
    }

    @GetMapping("/photo")
    public String photoUploadForm() {
        return "photo";
    }
}
