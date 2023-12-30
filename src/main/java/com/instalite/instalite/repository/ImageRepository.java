package com.instalite.instalite.repository;

import com.instalite.instalite.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Page<Image> findAllByIsPublic(Boolean b, Pageable pageable);
    Optional<Image> findByIdAndIsPublic(Long id, Boolean isPublic);
    Optional<Image> findById(UUID imageId);
}
