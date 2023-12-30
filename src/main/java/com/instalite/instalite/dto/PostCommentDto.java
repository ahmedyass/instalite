package com.instalite.instalite.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PostCommentDto {
    private String text;
    private UUID imageId;
}
