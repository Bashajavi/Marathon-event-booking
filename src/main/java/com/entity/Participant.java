package com.entity;

import jakarta.persistence.*;

@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String gender;
    private String email;
    private String category;

    @Column(name = "tshirt_size")
    private String tshirtSize;

    private String address;

    @Column(name = "bib_number")
    private Integer bibNumber; // Change int to Integer

    @Column(name = "finish_time")
    private String finishTime;

    private String status;
    // Default constructor
    public Participant() {
    }

 // Constructor with parameters
    public Participant(String name, int age, String gender, String email, String category,
                       String tshirtSize, String address, Integer bibNumber, String finishTime,
                       String status) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.category = category;
        this.tshirtSize = tshirtSize;
        this.address = address;
        this.bibNumber = bibNumber;
        this.finishTime = finishTime;
        this.status = status; // Initialize the status field
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTshirtSize() {
        return tshirtSize;
    }

    public void setTshirtSize(String tshirtSize) {
        this.tshirtSize = tshirtSize;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getBibNumber() {
        return bibNumber;
    }

    public void setBibNumber(Integer bibNumber) {
        this.bibNumber = bibNumber;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
