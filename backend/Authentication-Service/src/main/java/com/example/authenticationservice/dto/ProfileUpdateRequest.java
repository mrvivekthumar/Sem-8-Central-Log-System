package com.example.authenticationservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String name;
    private String email;

    private String bio;

    private List<String> skills;

    private String githubProfileLink;

    private String linkedInProfileLink;

    private String portfolioLink;

    private String phone;

    private String location;

    private Double ratings;

    private Integer projectsCompleted;

    private Integer currentProjects;
}
