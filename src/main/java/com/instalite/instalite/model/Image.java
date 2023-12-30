package com.instalite.instalite.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "images")
public class Image implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
