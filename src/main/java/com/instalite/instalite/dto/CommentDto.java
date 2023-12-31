package com.instalite.instalite.dto;

import com.instalite.instalite.model.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentDto {
    private UUID id;
    private UUID imageId;
    private UUID userId;
    private String username;
    private String text;
    private LocalDateTime timestamp;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .imageId(comment.getImage().getId())
            .userId(comment.getUser().getId())
            .username(comment.getUser().getUsername())
            .text(comment.getText())
            .timestamp(comment.getTimestamp())
            .build();
    }
}
