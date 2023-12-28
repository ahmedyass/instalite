package com.instalite.instalite.controller;

import com.instalite.instalite.model.Image;
import com.instalite.instalite.service.ImageService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/public/images")
    public ResponseEntity<?> listAllPublicImages(Pageable pageable) {
        return ResponseEntity.ok(imageService.getAllPublicImages(pageable));
    }

    @GetMapping("/private/images")
    public ResponseEntity<?> listAllPrivateImages(Pageable pageable) {
        return ResponseEntity.ok(imageService.getAllPrivateImages(pageable));
    }

    @GetMapping("/public/images/{id}")
    public ResponseEntity<?> downloadPublicImage(@PathVariable Long id) {
        try {
            URI imageUri = imageService.getImageUri(id, true);
            return ResponseEntity.ok().body(Map.of("url", imageUri.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/private/images/{id}")
    public ResponseEntity<?> downloadPrivateImage(@PathVariable Long id) {
        try {
            URI imageUri = imageService.getImageUri(id, false);
            return ResponseEntity.ok().body(Map.of("url", imageUri.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestParam("isPublic") Boolean isPublic) {
        try {
            Image image = imageService.uploadImage(file, title, description, isPublic);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(image.getId().toString())
                    .toUriString();

            return ResponseEntity.created(URI.create(fileDownloadUri)).body(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/images/{id}")
    public ResponseEntity<?> modifyImageMetadata(@PathVariable Long id,
                                                 @RequestBody Image image) {
        try {
            Image updatedImage = imageService.updateImageMetadata(id, image);
            return ResponseEntity.ok(updatedImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}


