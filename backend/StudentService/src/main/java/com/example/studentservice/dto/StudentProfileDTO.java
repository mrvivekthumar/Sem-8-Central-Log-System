package com.example.studentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String githubProfileLink;
    private String bio;
    private Float cgpa;
    private String imageUrl;
    @JsonProperty("linkedInProfileLink")
    private String linkedInUrl;
    private String location;
    private String portfolioLink;
    private List<String> skills;
    private Integer semesterNo;
}
