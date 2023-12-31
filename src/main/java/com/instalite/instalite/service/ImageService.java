package com.instalite.instalite.service;

import com.instalite.instalite.dto.ImageDTO;
import com.instalite.instalite.exception.UserNotFoundException;
import com.instalite.instalite.model.Image;
import com.instalite.instalite.model.User;
import com.instalite.instalite.repository.ImageRepository;
import com.instalite.instalite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    private final Path fileStorageLocation;

    @Autowired
    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        String relativePath = "/app/storage";
        this.fileStorageLocation = Paths.get(relativePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    private ImageDTO convertToDTO(Image image) {
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setTitle(image.getTitle());
        dto.setDescription(image.getDescription());
        dto.setIsPublic(image.getIsPublic());
        dto.setCreationDate(image.getCreationDate());
        dto.setUsername(image.getUser().getUsername());

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/").toUriString();
        dto.setUrl(baseUrl + image.getFilename());

        return dto;
    }
    public Page<ImageDTO> getAllPublicImages(int page, int size) {
        Page<Image> images = imageRepository.findAllByIsPublicOrderByCreationDateDesc(true, Pageable.ofSize(size).withPage(page));
        return images.map(this::convertToDTO);
    }

    public Page<ImageDTO> getAllPrivateImages(int page, int size) {
        Page<Image> images = imageRepository.findAllByIsPublicOrderByCreationDateDesc(false, Pageable.ofSize(size).withPage(page));
        return images.map(this::convertToDTO);
    }
    public URI getImageUri(UUID id, Boolean isPublic) throws Exception {
        Optional<Image> imageOptional = imageRepository.findByIdAndIsPublic(id, isPublic);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/").toUriString();
            String imageUrl = baseUrl + image.getFilename();
            return URI.create(imageUrl);
        } else {
            throw new Exception("Image not found with id: " + id + " and isPublic: " + isPublic);
        }
    }

    public ImageDTO uploadImage(MultipartFile file, String title, String description, Boolean isPublic, String issuerUsername) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Failed to store empty file.");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("Only image files are allowed.");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new Exception("Could not store file " + filename + ". Please try again!", ex);
        }

        User currentUser = userRepository.findByUsername(issuerUsername)
                .orElseThrow(UserNotFoundException::new);

        Image image = new Image();
        image.setFilename(filename);
        image.setTitle(title);
        image.setDescription(description);
        image.setIsPublic(isPublic);
        image.setCreationDate(new Date());
        image.setUser(currentUser);

        image = imageRepository.save(image);

        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setTitle(image.getTitle());
        dto.setDescription(image.getDescription());
        dto.setIsPublic(image.getIsPublic());
        dto.setCreationDate(image.getCreationDate());
        dto.setUsername(image.getUser().getUsername());

        return dto;
    }

    public void deleteImage(UUID id) throws Exception {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            Path filePath = this.fileStorageLocation.resolve(image.getFilename()).normalize();

            if (!Files.exists(filePath)) {
                throw new Exception("File not found: " + image.getFilename());
            }
            try {
                Files.delete(filePath);
            } catch (IOException ex) {
                throw new Exception("Could not delete file: " + image.getFilename(), ex);
            }

            imageRepository.deleteById(id);

        } else {
            throw new Exception("Image not found with id: " + id);
        }
    }

    public ImageDTO updateImageMetadata(UUID id, Image newImage) throws Exception {
        return imageRepository.findById(id)
                .map(image -> {
                    if (newImage.getTitle() != null) {
                        image.setTitle(newImage.getTitle());
                    }
                    if (newImage.getDescription() != null) {
                        image.setDescription(newImage.getDescription());
                    }
                    if (newImage.getIsPublic() != null) {
                        image.setIsPublic(newImage.getIsPublic());
                    }

                    var updatedImage = imageRepository.save(image);
                    var dto = new ImageDTO();
                    dto.setId(updatedImage.getId());
                    dto.setTitle(updatedImage.getTitle());
                    dto.setDescription(updatedImage.getDescription());
                    dto.setIsPublic(updatedImage.getIsPublic());
                    dto.setCreationDate(updatedImage.getCreationDate());
                    dto.setUsername(updatedImage.getUser().getUsername());
                    return dto;
                })
                .orElseThrow(() -> new Exception("Image not found with id " + id));
    }
}

