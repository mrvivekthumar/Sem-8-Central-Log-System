package com.example.studentservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedProjectDTO {
    // From StudentProject
    private int applicationId;
    private int projectId;
    private String projectName;
    private String status;
    private LocalDate applicationDate;

    // From FacultyService Project
    private String title;
    private String description;
    private String domain;
    private Integer teamSize;
    private String duration;
    private List<String> skills;
    private LocalDateTime deadline;
    private String facultyName;
}
