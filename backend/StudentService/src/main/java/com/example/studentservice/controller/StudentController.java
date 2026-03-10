package com.example.studentservice.controller;

import com.example.studentservice.client.FacultyInterface;
import com.example.studentservice.client.dto.Project;
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
@RequestMapping("/students") // Under /student context-path, accessible at /student/students
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private FacultyInterface facultyInterface;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("StudentController initialized and ready!");
        logger.info("Context Path: /student/students");
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
     * Get all visible projects from Faculty Service (for browsing)
     * GET /student/projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects(
            @RequestHeader("X-User-Id") String studentId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: GET /student/projects");
        logger.info("Student ID from header: {}", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            // Fetch visible projects from Faculty Service (not student's applied projects)
            ResponseEntity<List<Project>> response = facultyInterface.getVisibleProjects();
            List<Project> projects = response.getBody();
            logger.info("Fetched {} visible projects from Faculty Service",
                    projects != null ? projects.size() : 0);
            logger.info("===============================================");
            return ResponseEntity.ok(projects != null ? projects : List.of());

        } catch (Exception e) {
            logger.error("Failed to fetch visible projects: {}", e.getMessage(), e);
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
     * Get completed projects for a student
     * GET /student/students/{studentId}/completed-projects
     */
    @GetMapping("/{studentId}/completed-projects")
    public ResponseEntity<List<com.example.studentservice.dto.CompletedProjectDTO>> getCompletedProjects(
            @PathVariable int studentId,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: GET /students/{}/completed-projects", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            List<com.example.studentservice.dto.CompletedProjectDTO> completedProjects = studentService
                    .getCompletedProjects(studentId);
            logger.info("Found {} completed projects for student: {}", completedProjects.size(), studentId);
            logger.info("===============================================");
            return ResponseEntity.ok(completedProjects);

        } catch (Exception e) {
            logger.error("Failed to fetch completed projects for student {}: {}",
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

    /**
     * Update student profile by ID (path variable)
     * PUT /student/students/{studentId}
     * Used by frontend when updating profile with ID in URL
     */
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentProfileDTO> updateProfileById(
            @PathVariable String studentId,
            @RequestBody StudentProfileDTO profileDTO,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: PUT /student/students/{}", studentId);
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

    /**
     * Update students availability after project assignment
     * PUT /student/students/available/{projectId}
     * Called by FacultyService after assigning students to a project
     */
    @PutMapping("/available/{projectId}")
    public ResponseEntity<String> updateStudentsAvailable(
            @PathVariable int projectId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: PUT /students/available/{}", projectId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            studentService.updateStudentsAvailability(projectId);
            logger.info("Students availability updated for project: {}", projectId);
            logger.info("===============================================");
            return ResponseEntity.ok("Students availability updated successfully");

        } catch (Exception e) {
            logger.error("Failed to update students availability for project {}: {}",
                    projectId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Get students by IDs (bulk operation)
     * POST /student/students/byIds
     * Called by FacultyService
     */
    @PostMapping("/byIds")
    public ResponseEntity<List<com.example.studentservice.domain.Student>> getStudentsByIds(
            @RequestBody List<Integer> studentIds,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: POST /students/byIds - Fetching students: {}", studentIds);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            List<com.example.studentservice.domain.Student> students = studentService.getStudentsByIds(studentIds);
            logger.info("Found {} students", students.size());
            logger.info("===============================================");
            return ResponseEntity.ok(students);

        } catch (Exception e) {
            logger.error("Failed to fetch students by IDs: {}", e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Make student unavailable
     * PUT /student/students/{studentId}/unavailable
     * Called by FacultyService when student is assigned to a project
     */
    @PutMapping("/{studentId}/unavailable")
    public ResponseEntity<Void> makeStudentUnavailable(
            @PathVariable int studentId,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: PUT /students/{}/unavailable", studentId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            studentService.makeStudentUnavailable(studentId);
            logger.info("Student {} marked as unavailable", studentId);
            logger.info("===============================================");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Failed to make student {} unavailable: {}",
                    studentId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    /**
     * Get all student IDs
     * GET /student/students/allIds
     * Called by FacultyService for sending notifications to all students
     */
    @GetMapping("/allIds")
    public ResponseEntity<List<Integer>> getAllStudentIds(HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: GET /students/allIds - Fetching all student IDs");
        try {
            List<Integer> ids = studentService.getAllStudentIds();
            logger.info("Found {} student IDs", ids.size());
            logger.info("===============================================");
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            logger.error("Failed to fetch all student IDs: {}", e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }
}
