package com.instalite.instalite.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ImageDTO {
    private UUID id;
    private String title;
    private String description;
    private Boolean isPublic;
    private String url;
    private Date creationDate;
}