package com.instalite.instalite.model;

import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "images")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "date")
    private Date creationDate;
}
