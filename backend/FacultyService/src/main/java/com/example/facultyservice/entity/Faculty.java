package com.example.facultyservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faculty")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_id")
    private Integer fId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    // ✅ Profile fields matching frontend StudentProfile.jsx
    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection
    @CollectionTable(name = "faculty_skills", joinColumns = @JoinColumn(name = "faculty_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(name = "github_profile_link")
    private String githubProfileLink;

    @Column(name = "linkedin_profile_link")
    private String linkedInProfileLink;

    @Column(name = "portfolio_link")
    private String portfolioLink;

    @Column(name = "phone")
    private String phone;

    @Column(name = "location")
    private String location;

    // ✅ Stats fields
    @Column(name = "ratings")
    private Double ratings = 0.0;

    @Column(name = "projects_completed")
    private Integer projectsCompleted = 0;

    @Column(name = "current_projects")
    private Integer currentProjects = 0;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        if (skills == null) {
            skills = new ArrayList<>();
        }
        if (ratings == null) {
            ratings = 0.0;
        }
        if (projectsCompleted == null) {
            projectsCompleted = 0;
        }
        if (currentProjects == null) {
            currentProjects = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    // ✅ Convenience method for frontend compatibility
    public Integer getId() {
        return fId;
    }

    public void setId(Integer id) {
        this.fId = id;
    }
}
