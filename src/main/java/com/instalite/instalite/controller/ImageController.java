package com.instalite.instalite.controller;

import com.instalite.instalite.model.Image;
import com.instalite.instalite.service.ImageService;

import jakarta.servlet.ServletContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final ImageService imageService;
    private final ServletContext servletContext;

    public ImageController(ImageService imageService, ServletContext servletContext) {
        this.imageService = imageService;
        this.servletContext = servletContext;
    }

    private String determineContentType(String filename) {
        String mimeType = servletContext.getMimeType(filename);
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    @GetMapping
    public ResponseEntity<Page<Image>> getAllImages(@RequestParam(required = false) String title, Pageable pageable) {
        if (title != null) {
            return ResponseEntity.ok(imageService.findByTitle(title, pageable));
        }
        return ResponseEntity.ok(imageService.findAll(pageable));
    }
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource image = imageService.loadAsResource(filename);
        String contentType = determineContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        Set<String> allowedMediaTypes = Set.of(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_PNG_VALUE,
                MediaType.IMAGE_GIF_VALUE
        );

        try {
            if (!allowedMediaTypes.contains(file.getContentType())) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Unsupported media type: " + file.getContentType());
            }

            imageService.store(file);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

}
