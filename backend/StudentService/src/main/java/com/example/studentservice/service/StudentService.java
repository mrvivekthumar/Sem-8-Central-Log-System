package com.example.studentservice.service;

import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.dto.StudentDashboardDTO;
import com.example.studentservice.dto.StudentProfileDTO;

import java.util.List;

public interface StudentService {

    /**
     * Get student dashboard data
     * 
     * @param studentId Student ID
     * @return Dashboard data
     */
    StudentDashboardDTO getDashboard(String studentId);

    /**
     * Get all projects for a student
     * 
     * @param studentId Student ID
     * @return List of student projects
     */
    List<StudentProject> getProjects(String studentId);

    /**
     * Get project by ID
     * 
     * @param projectId Project ID
     * @return Student project
     */
    StudentProject getProjectById(Long projectId);

    /**
     * Get student profile
     * 
     * @param studentId Student ID
     * @return Student profile
     */
    StudentProfileDTO getProfile(String studentId);

    /**
     * Update student profile
     * 
     * @param studentId  Student ID
     * @param profileDTO Profile data
     * @return Updated profile
     */
    StudentProfileDTO updateProfile(String studentId, StudentProfileDTO profileDTO);
}
