package com.instalite.instalite.repository;

import com.instalite.instalite.model.Comment;
import com.instalite.instalite.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findAllByImageOrderByTimestampDesc(Image image, Pageable pageable);
    void deleteAllByImage(Image image);
}