package com.instalite.instalite.service;

import com.instalite.instalite.dto.CommentDto;
import com.instalite.instalite.dto.PaginatedResultsDto;
import com.instalite.instalite.exception.InvalidRoleException;
import com.instalite.instalite.exception.UserNotFoundException;
import com.instalite.instalite.exception.WrongEndpointException;
import com.instalite.instalite.model.Comment;
import com.instalite.instalite.repository.CommentRepository;
import com.instalite.instalite.repository.ImageRepository;
import com.instalite.instalite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.instalite.instalite.model.UserRole.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public PaginatedResultsDto<CommentDto> paginatedComments(UUID imageId, int page, int size, Boolean isPublic, Principal issuer) {
        var image = imageRepository.findById(imageId).orElseThrow();
        // Check if using correct endpoint
        if (image.getIsPublic() != isPublic) {
            throw new WrongEndpointException();
        }
        // Check rights to view comments
        if (!isPublic) {
            var user = userRepository.findByUsername(issuer.getName())
                .orElseThrow(UserNotFoundException::new);
            // Only admin and privileged users can view private comments
            if (!user.getRole().equals(ADMINISTRATOR) && !user.getRole().equals(PRIVILEGED_USER)) {
                throw new InvalidRoleException();
            }
        }
        var comments = commentRepository.findAllByImage(image, Pageable.ofSize(size).withPage(page));

        var paginatedResults = PaginatedResultsDto.from(comments.map(CommentDto::from));
        paginatedResults.setPage(comments.getNumber());
        paginatedResults.setItemsPerPage(comments.getNumberOfElements());
        paginatedResults.setItemCount(comments.getTotalElements());
        paginatedResults.setPageCount(comments.getTotalPages());
        return paginatedResults;
    }

    public CommentDto create(UUID imageId, Boolean isPublic, String text, String issuerUsername) {
        var user = userRepository.findByUsername(issuerUsername)
            .orElseThrow(UserNotFoundException::new);
        var image = imageRepository.findById(imageId).orElseThrow();
        // Check if using correct endpoint
        if (image.getIsPublic() != isPublic) {
            throw new WrongEndpointException();
        }
        // Check rights to create comments
        if (!isPublic) {
            // Only admin and privileged users can create comments on private images
            if (!user.getRole().equals(ADMINISTRATOR) && !user.getRole().equals(PRIVILEGED_USER)) {
                throw new InvalidRoleException();
            }
        }
        var comment = new Comment();
        comment.setImage(image);
        comment.setUser(user);
        comment.setText(text);
        comment.setTimestamp(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentDto.from(comment);
    }

    public void deleteById(UUID commentId, UUID imageId, Boolean isPublic, String issuerUsername) {
        deleteAndEditChecks(commentId, imageId, isPublic, issuerUsername);

        commentRepository.deleteById(commentId);
    }

    public CommentDto updateById(UUID commentId, UUID imageId, Boolean isPublic, String text, String issuerUsername) {
        var comment = deleteAndEditChecks(commentId, imageId, isPublic, issuerUsername);

        comment.setText(text);
        comment = commentRepository.save(comment);
        return CommentDto.from(comment);
    }

    // Helper method for delete and update
    private Comment deleteAndEditChecks(UUID commentId, UUID imageId, Boolean isPublic, String issuerUsername) {
        var issuer = userRepository.findByUsername(issuerUsername)
            .orElseThrow(UserNotFoundException::new);
        var image = imageRepository.findById(imageId).orElseThrow();
        var comment = commentRepository.findById(commentId).orElseThrow();
        // Check if comment belongs to image
        if (!comment.getImage().equals(image)) {
            throw new WrongEndpointException();
        }
        // Check if using correct endpoint
        if (image.getIsPublic() != isPublic) {
            throw new WrongEndpointException();
        }
        // Check if user is owner of comment or admin
        if (!comment.getUser().equals(issuer) && !issuer.getRole().equals(ADMINISTRATOR)) {
            throw new WrongEndpointException();
        }

        return comment;
    }
}
