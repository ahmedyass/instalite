package com.instalite.instalite.dto;

import com.instalite.instalite.model.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private String username;
    private String text;
    private LocalDateTime timestamp;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
            .username(comment.getUser().getUsername())
            .text(comment.getText())
            .timestamp(comment.getTimestamp())
            .build();
    }
}
