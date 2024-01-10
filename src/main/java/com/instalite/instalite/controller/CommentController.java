package com.instalite.instalite.controller;

import com.instalite.instalite.dto.CommentDto;
import com.instalite.instalite.dto.PaginatedResultsDto;
import com.instalite.instalite.exception.WrongEndpointException;
import com.instalite.instalite.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    private boolean visiblityToBoolean(String visibility) {
        if (visibility.equals("public")) {
            return true;
        } else if (visibility.equals("private")) {
            return false;
        } else {
            throw new WrongEndpointException();
        }
    }

    @GetMapping("{visibility}/images/{imageId}/comments")
    public ResponseEntity<PaginatedResultsDto<CommentDto>> getPaginatedComments(@PathVariable String visibility,
                                                                                @PathVariable UUID imageId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                Principal principal) {
        var searchResultsDto = commentService.paginatedComments(imageId,
                page, size, visiblityToBoolean(visibility), principal);
        return ResponseEntity.ok(searchResultsDto);
    }

    @PostMapping("{visibility}/images/{imageId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> create(@PathVariable String visibility,
                                             @PathVariable UUID imageId,
                                             @RequestBody String text,
                                             Principal principal) {
        var comment = commentService.create(imageId, visiblityToBoolean(visibility), text, principal.getName());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("{visibility}/images/{imageId}/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteById(@PathVariable String visibility,
                                           @PathVariable UUID imageId,
                                           @PathVariable UUID commentId,
                                           Principal principal) {
        commentService.deleteById(commentId, imageId, visiblityToBoolean(visibility), principal.getName());
        return ResponseEntity.status(204).build();
    }

    @PutMapping("{visibility}/images/{imageId}/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> updateById(@PathVariable String visibility,
                                                 @PathVariable UUID imageId,
                                                 @PathVariable UUID commentId,
                                                 @RequestBody String text,
                                                 Principal principal) {
        var comment = commentService.updateById(commentId, imageId, visiblityToBoolean(visibility), text,
                principal.getName());
        return ResponseEntity.ok(comment);
    }

}
