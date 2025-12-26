package com.example.studentservice.dto;

import com.example.studentservice.domain.StudentProject;
import lombok.Data;

import java.util.List;

@Data
public class StudentDashboardDTO {
    private String studentId;
    private String studentName;
    private int totalProjects;
    private List<StudentProject> projects;
}
