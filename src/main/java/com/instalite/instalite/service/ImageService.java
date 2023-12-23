package com.instalite.instalite.service;

import com.instalite.instalite.model.Image;
import com.instalite.instalite.repository.ImageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final Path rootLocation = Paths.get("/opt/app/data");
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }
    public Page<Image> findAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }
    public Page<Image> findAllPrivate(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }
    public Page<Image> findByName(String name, Pageable pageable) {
        return imageRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    public Optional<Image> get(Long id) {
        return imageRepository.findById(id);
    }
    public void delete(Long id) { imageRepository.deleteById(id); }
    public Optional<Image> updateImage(Long id, Image imageDetails) {
        return imageRepository.findById(id)
                .map(image -> {
                    if (imageDetails.getTitle() != null) {
                        image.setTitle(imageDetails.getTitle());
                    }
                    if (imageDetails.getDescription() != null) {
                        image.setDescription(imageDetails.getDescription());
                    }
                    if (imageDetails.isPublic() != image.isPublic()) {
                        image.setPublic(imageDetails.isPublic());
                    }
                    return imageRepository.save(image);
                });
    }
    public void store(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
