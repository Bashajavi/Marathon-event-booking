package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "poster")
public class Poster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @Column(name = "image_name")
    private String imageName; // Field to store the name of the uploaded image

    // Constructors
    public Poster() {
    }

    public Poster(Long id, String name, String imageName) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
