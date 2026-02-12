package com.example.facultyservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.example.facultyservice.entity.Project;
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
}
