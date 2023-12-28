package com.instalite.instalite.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ImageDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean isPublic;
    private String url;
    private Date creationDate;
}