package com.example.studentservice.controller;

import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.dto.StudentDashboardDTO;
import com.example.studentservice.dto.StudentProfileDTO;
import com.example.studentservice.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("StudentController initialized and ready!");
        logger.info("Base path: /student");
        logger.info("Endpoints: GET /dashboard, GET /projects, GET /profile");
        logger.info("=================================================");
    }

    /**
     * Get student dashboard
     * GET /student/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardDTO> getDashboard(
            @RequestHeader("X-User-Id") String studentId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: GET /student/dashboard");
        logger.info("Student ID from header: {}", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            StudentDashboardDTO dashboard = studentService.getDashboard(studentId);
            logger.info("Dashboard fetched successfully for student: {}", studentId);
            logger.info("===============================================");
            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            logger.error("Failed to fetch dashboard for student {}: {}",
                    studentId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Get all student projects
     * GET /student/projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<StudentProject>> getProjects(
            @RequestHeader("X-User-Id") String studentId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: GET /student/projects");
        logger.info("Student ID from header: {}", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            List<StudentProject> projects = studentService.getProjects(studentId);
            logger.info("Fetched {} projects for student: {}", projects.size(), studentId);
            logger.info("===============================================");
            return ResponseEntity.ok(projects);

        } catch (Exception e) {
            logger.error("Failed to fetch projects for student {}: {}",
                    studentId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Get project by ID
     * GET /student/projects/{projectId}
     */
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<StudentProject> getProjectById(
            @PathVariable Long projectId,
            @RequestHeader("X-User-Id") String studentId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: GET /student/projects/{}", projectId);
        logger.info("Student ID: {}", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            StudentProject project = studentService.getProjectById(projectId);
            logger.info("Project fetched: {} (ID: {})", project.getProjectId(), projectId);
            logger.info("===============================================");
            return ResponseEntity.ok(project);

        } catch (Exception e) {
            logger.error("Failed to fetch project {} for student {}: {}",
                    projectId, studentId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Get student profile
     * GET /student/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDTO> getProfile(
            @RequestHeader("X-User-Id") String studentId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: GET /student/profile");
        logger.info("Student ID from header: {}", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            StudentProfileDTO profile = studentService.getProfile(studentId);
            logger.info("Profile fetched successfully for student: {}", studentId);
            logger.info("===============================================");
            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            logger.error("Failed to fetch profile for student {}: {}",
                    studentId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Update student profile
     * PUT /student/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<StudentProfileDTO> updateProfile(
            @RequestHeader("X-User-Id") String studentId,
            @RequestBody StudentProfileDTO profileDTO,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: PUT /student/profile");
        logger.info("Student ID from header: {}", studentId);
        logger.debug("Profile data: {}", profileDTO);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            StudentProfileDTO updated = studentService.updateProfile(studentId, profileDTO);
            logger.info("Profile updated successfully for student: {}", studentId);
            logger.info("===============================================");
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            logger.error("Failed to update profile for student {}: {}",
                    studentId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }
}
