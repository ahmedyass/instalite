package com.instalite.instalite.repository;

import com.instalite.instalite.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Long> {
    Page<Image> findAll(Pageable pageable);
    Page<Image> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
