package com.instalite.instalite.controller;

import com.instalite.instalite.model.Image;
import com.instalite.instalite.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/public/images")
    public ResponseEntity<?> listAllPublicImages(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(imageService.getAllPublicImages(page, size));
    }

    @GetMapping("/private/images")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'PRIVILEGED_USER')")
    public ResponseEntity<?> listAllPrivateImages(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(imageService.getAllPrivateImages(page, size));
    }

    @GetMapping("/public/images/{id}")
    public ResponseEntity<?> downloadPublicImage(@PathVariable UUID id) {
        try {
            URI imageUri = imageService.getImageUri(id, true);
            return ResponseEntity.ok().body(Map.of("url", imageUri.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/private/images/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'PRIVILEGED_USER')")
    public ResponseEntity<?> downloadPrivateImage(@PathVariable UUID id) {
        try {
            URI imageUri = imageService.getImageUri(id, false);
            return ResponseEntity.ok().body(Map.of("url", imageUri.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/images")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestParam("isPublic") Boolean isPublic,
                                         Principal principal) {
        try {
            Image image = imageService.uploadImage(file, title, description, isPublic, principal.getName());
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
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/images/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> modifyImageMetadata(@PathVariable UUID id,
                                                 @RequestBody Image image) {
        try {
            Image updatedImage = imageService.updateImageMetadata(id, image);
            return ResponseEntity.ok(updatedImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}


