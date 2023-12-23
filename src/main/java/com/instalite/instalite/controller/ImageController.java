package com.instalite.instalite.controller;

import com.instalite.instalite.model.Image;
import com.instalite.instalite.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<Page<Image>> getAllLivreurs(@RequestParam(required = false) String name, Pageable pageable) {
        if (name != null) {
            return ResponseEntity.ok(imageService.findByName(name, pageable));
        }
        return ResponseEntity.ok(imageService.findAll(pageable));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            imageService.store(file);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

}
