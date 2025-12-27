package com.example.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    // ✅ CHANGED: Use String to match controller parameter
    private String studentId;
    private String name;
    private String email;
    private String phone;

    // Optional fields you might want to add
    private String githubProfileLink;
    private String bio;
    private Float cgpa;
    private String imageUrl;
    private String linkedInUrl;
}
