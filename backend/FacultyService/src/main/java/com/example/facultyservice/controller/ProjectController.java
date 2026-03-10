package com.example.facultyservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.facultyservice.client.StudentInterface;
import com.example.facultyservice.entity.Project;
import com.example.facultyservice.entity.Status;
import com.example.facultyservice.repository.ProjectRepository;
import com.example.facultyservice.service.ProjectService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Project Controller
 * 
 * Handles all project-related endpoints.
 * Routes via Gateway: /api/projects/** -> /faculty/projects/**
 * Also handles: /api/faculty/projects/** -> /faculty/projects/**
 */
@RestController
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentInterface studentInterface;

    @PostConstruct
    public void init() {
        log.info("=================================================");
        log.info("ProjectController initialized and ready!");
        log.info("Context Path: /faculty/projects");
        log.info("Available Endpoints:");
        log.info("  GET    /faculty/projects");
        log.info("  GET    /faculty/projects/{id}");
        log.info("  GET    /faculty/projects/faculty/{facultyId}");
        log.info("  GET    /faculty/projects/visible");
        log.info("  POST   /faculty/projects");
        log.info("  POST   /faculty/projects/{facultyId}");
        log.info("  PUT    /faculty/projects/{id}");
        log.info("  DELETE /faculty/projects/{id}");
        log.info("=================================================");
    }

    /**
     * Get all projects
     * GET /api/projects or /api/faculty/projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /projects - Fetching all projects");

        // Extract user info from headers (set by Gateway)
        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");
        log.info("Controller: User ID: {}, Role: {}", userId, userRole);

        try {
            List<Project> projects = projectService.getAllProjects();
            log.info("Controller: Successfully fetched {} projects", projects.size());
            log.info("===============================================");
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            log.error("Controller: Error fetching projects: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer id, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /projects/{} - Fetching project by ID", id);

        try {
            return projectService.getProjectById(id)
                    .map(project -> {
                        log.info("Controller: Project found - ID: {}, Title: {}", id, project.getTitle());
                        log.info("===============================================");
                        return ResponseEntity.ok(project);
                    })
                    .orElseGet(() -> {
                        log.warn("Controller: Project not found with ID: {}", id);
                        log.info("===============================================");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Controller: Error fetching project by ID {}: {}", id, e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Get projects for a specific faculty
     * GET /api/projects/faculty/{facultyId}
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Project>> getProjectsByFacultyId(
            @PathVariable Long facultyId,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /projects/faculty/{} - Fetching projects for faculty", facultyId);

        try {
            List<Project> projects = projectService.getProjectsByFacultyId(facultyId);
            log.info("Controller: Found {} projects for faculty {}", projects.size(), facultyId);
            log.info("===============================================");
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            log.error("Controller: Error fetching projects for faculty {}: {}", facultyId, e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Get visible projects (open for applications)
     * GET /api/projects/visible
     */
    @GetMapping("/visible")
    public ResponseEntity<List<Project>> getVisibleProjects(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /projects/visible - Fetching visible projects");

        try {
            List<Project> projects = projectService.getVisibleProjects();
            log.info("Controller: Found {} visible projects", projects.size());
            log.info("===============================================");
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            log.error("Controller: Error fetching visible projects: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Create a new project (with facultyId in path)
     * POST /api/projects/{facultyId}
     */
    @PostMapping("/{facultyId}")
    public ResponseEntity<Project> createProjectForFaculty(
            @PathVariable Integer facultyId,
            @Valid @RequestBody Project project,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: POST /projects/{} - Creating new project: {}", facultyId, project.getTitle());

        try {
            Project createdProject = projectService.createProject(project, facultyId);
            log.info("Controller: Project created successfully - ID: {}", createdProject.getProjectId());
            log.info("===============================================");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (RuntimeException e) {
            log.error("Controller: Error creating project: {}", e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new project (facultyId from header)
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<?> createProject(
            @Valid @RequestBody Project project,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: POST /projects - Creating new project: {}", project.getTitle());

        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null) {
            log.error("Controller: X-User-Id header not found");
            Map<String, String> error = new HashMap<>();
            error.put("error", "User ID not found in request");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Integer facultyId = Integer.parseInt(userIdHeader);
            Project createdProject = projectService.createProject(project, facultyId);
            log.info("Controller: Project created successfully - ID: {}", createdProject.getProjectId());
            log.info("===============================================");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (NumberFormatException e) {
            log.error("Controller: Invalid user ID format: {}", userIdHeader);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid user ID format");
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            log.error("Controller: Error creating project: {}", e.getMessage(), e);
            log.info("===============================================");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Integer id,
            @Valid @RequestBody Project projectDetails,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: PUT /projects/{} - Updating project", id);

        try {
            Project updatedProject = projectService.updateProject(id, projectDetails);
            log.info("Controller: Project updated successfully - ID: {}", id);
            log.info("===============================================");
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            log.error("Controller: Error updating project {}: {}", id, e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer id, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: DELETE /projects/{} - Deleting project", id);

        try {
            projectService.deleteProject(id);
            log.info("Controller: Project deleted successfully - ID: {}", id);
            log.info("===============================================");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Controller: Error deleting project {}: {}", id, e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get projects by IDs (bulk operation)
     * POST /api/projects/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Project>> getProjectsByIds(
            @RequestBody List<Integer> projectIds,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: POST /projects/bulk - Fetching projects by IDs: {}", projectIds);

        try {
            List<Project> projects = projectService.getProjectsByIds(projectIds);
            log.info("Controller: Found {} projects", projects.size());
            log.info("===============================================");
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            log.error("Controller: Error fetching projects by IDs: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Check if project is complete
     * GET /api/projects/{projectId}/is-complete
     * Called by StudentService
     */
    @GetMapping("/{projectId}/is-complete")
    public ResponseEntity<Boolean> isProjectComplete(
            @PathVariable Integer projectId,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /projects/{}/is-complete", projectId);

        try {
            return projectService.getProjectById(projectId)
                    .map(project -> {
                        boolean isComplete = project.getStatus() == Status.COMPLETED;
                        log.info("Controller: Project {} is complete: {}", projectId, isComplete);
                        log.info("===============================================");
                        return ResponseEntity.ok(isComplete);
                    })
                    .orElseGet(() -> {
                        log.warn("Controller: Project not found: {}", projectId);
                        log.info("===============================================");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Controller: Error checking project completion: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Mark a project as completed
     * PUT /api/projects/{projectId}/complete
     * Also updates all APPROVED student applications to COMPLETED
     */
    @PutMapping("/{projectId}/complete")
    public ResponseEntity<?> completeProject(
            @PathVariable Integer projectId,
            HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: PUT /projects/{}/complete - Marking project as completed", projectId);

        try {
            Optional<Project> projectOpt = projectService.getProjectById(projectId);
            if (projectOpt.isEmpty()) {
                log.warn("Controller: Project not found: {}", projectId);
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            project.setStatus(Status.COMPLETED);
            projectRepository.save(project);
            log.info("Controller: Project {} marked as COMPLETED", projectId);

            // Update all APPROVED student applications to COMPLETED
            try {
                ResponseEntity<List<Map<String, Object>>> applicationsResp = studentInterface
                        .getApplicationsByProject(projectId);
                List<Map<String, Object>> applications = applicationsResp.getBody();
                if (applications != null) {
                    for (Map<String, Object> app : applications) {
                        String status = String.valueOf(app.get("status"));
                        if ("APPROVED".equals(status) || "IN_PROGRESS".equals(status)) {
                            Object studentObj = app.get("student");
                            if (studentObj instanceof Map) {
                                Object studentIdObj = ((Map<?, ?>) studentObj).get("studentId");
                                if (studentIdObj != null) {
                                    int studentId = Integer.parseInt(String.valueOf(studentIdObj));
                                    Map<String, String> body = new java.util.HashMap<>();
                                    body.put("status", "COMPLETED");
                                    studentInterface.updateApplicationStatus(studentId, projectId, body);
                                    log.info("Controller: Updated student {} application to COMPLETED", studentId);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Controller: Error updating student application statuses: {}", e.getMessage(), e);
            }

            log.info("===============================================");
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            log.error("Controller: Error completing project {}: {}", projectId, e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all applications for a project
     * GET /api/projects/{projectId}/applications
     * Proxies to StudentService: GET /student/studentProject/students/{projectId}
     */
    @GetMapping("/{projectId}/applications")
    public ResponseEntity<List<Map<String, Object>>> getApplications(@PathVariable int projectId) {
        log.info("Controller: GET /projects/{}/applications - Fetching applications", projectId);
        try {
            ResponseEntity<List<Map<String, Object>>> response = studentInterface.getApplicationsByProject(projectId);
            log.info("Controller: Found {} applications for project {}",
                    response.getBody() != null ? response.getBody().size() : 0, projectId);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Controller: Error fetching applications for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Accept a student application
     * POST /api/projects/{projectId}/accept/{studentId}
     * Proxies to StudentService: PUT
     * /student/studentProject/updateStatus/{studentId}/{projectId}
     */
    @PostMapping("/{projectId}/accept/{studentId}")
    public ResponseEntity<Object> acceptStudent(
            @PathVariable int projectId,
            @PathVariable int studentId) {
        log.info("Controller: POST /projects/{}/accept/{} - Accepting student", projectId, studentId);
        try {
            Map<String, String> body = new HashMap<>();
            body.put("status", "APPROVED");
            ResponseEntity<Object> response = studentInterface.updateApplicationStatus(studentId, projectId, body);
            log.info("Controller: Student {} accepted for project {}", studentId, projectId);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Controller: Error accepting student {} for project {}: {}", studentId, projectId, e.getMessage(),
                    e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to accept student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Reject a student application
     * POST /api/projects/{projectId}/reject/{studentId}
     * Proxies to StudentService: PUT
     * /student/studentProject/updateStatus/{studentId}/{projectId}
     */
    @PostMapping("/{projectId}/reject/{studentId}")
    public ResponseEntity<Object> rejectStudent(
            @PathVariable int projectId,
            @PathVariable int studentId,
            @RequestBody(required = false) Map<String, String> body) {
        log.info("Controller: POST /projects/{}/reject/{} - Rejecting student", projectId, studentId);
        try {
            Map<String, String> statusBody = new HashMap<>();
            statusBody.put("status", "REJECTED");
            ResponseEntity<Object> response = studentInterface.updateApplicationStatus(studentId, projectId,
                    statusBody);
            log.info("Controller: Student {} rejected for project {}", studentId, projectId);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Controller: Error rejecting student {} for project {}: {}", studentId, projectId, e.getMessage(),
                    e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to reject student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
