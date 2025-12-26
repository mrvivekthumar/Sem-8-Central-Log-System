package com.example.facultyservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fId; // Changed from Long to Integer to match repository

    private String name;
    private String email;
    private String password;
    private String department;

    // Add these fields for profile (optional)
    private String bio;
    private String phone;
    private String location;

    @Column(name = "github_profile_link")
    private String githubProfileLink;

    @Column(name = "linked_in_profile_link")
    private String linkedInProfileLink;

    @Column(name = "portfolio_link")
    private String portfolioLink;
}
