package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contact") // Define the table name explicitly
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String message;
    private String status;

    // Default constructor
    public Contact() {
    }

    // Constructor with parameters
    public Contact(String name, String email, String phone, String message, String status) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.status = status;
    }

    // Getters and setters for all fields

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
