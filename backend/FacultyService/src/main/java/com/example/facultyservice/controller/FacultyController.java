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

import com.example.facultyservice.client.AuthInterface;
import com.example.facultyservice.entity.Faculty;
import com.example.facultyservice.entity.Project;
import com.example.facultyservice.service.FacultyService;
import com.example.facultyservice.service.ProjectService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("") // Context-path /faculty already provides the prefix
@Slf4j
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AuthInterface authInterface;

    @PostConstruct
    public void init() {
        log.info("=================================================");
        log.info("FacultyController initialized and ready!");
        log.info("Context Path: /faculty");
        log.info("Available Endpoints:");
        log.info("  GET    /faculty/dashboard");
        log.info("  GET    /faculty/profile");
        log.info("  GET    /faculty");
        log.info("  GET    /faculty/{id}");
        log.info("  GET    /faculty/email/{email}");
        log.info("  POST   /faculty");
        log.info("  PUT    /faculty/{id}");
        log.info("  DELETE /faculty/{id}");
        log.info("  GET    /faculty/count");
        log.info("  GET    /faculty/emails");
        log.info("  GET    /faculty/exists/{email}");
        log.info("=================================================");
    }

    /**
     * Get faculty dashboard data
     * GET /api/faculty/dashboard
     * Returns projects created by the authenticated faculty
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/dashboard - Fetching dashboard data");

        String userIdHeader = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");
        log.info("Controller: User ID: {}, Role: {}", userIdHeader, userRole);

        if (userIdHeader == null) {
            log.error("Controller: X-User-Id header not found");
            Map<String, String> error = new HashMap<>();
            error.put("error", "User ID not found in request");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Long facultyId = Long.parseLong(userIdHeader);
            List<Project> projects = projectService.getProjectsByFacultyId(facultyId);

            // Build dashboard response
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("projects", projects);
            dashboard.put("totalProjects", projects.size());
            dashboard.put("activeProjects", projects.stream()
                    .filter(p -> "IN_PROGRESS".equals(p.getStatus().name()))
                    .count());
            dashboard.put("completedProjects", projects.stream()
                    .filter(p -> "COMPLETED".equals(p.getStatus().name()))
                    .count());
            dashboard.put("pendingApplications", projects.stream()
                    .filter(p -> "OPEN_FOR_APPLICATIONS".equals(p.getStatus().name()))
                    .count());

            log.info("Controller: Dashboard loaded - {} projects", projects.size());
            log.info("===============================================");
            return ResponseEntity.ok(dashboard);
        } catch (NumberFormatException e) {
            log.error("Controller: Invalid user ID format: {}", userIdHeader);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid user ID format");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Controller: Error fetching dashboard: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    /**
     * Get faculty profile
     * GET /api/faculty/profile
     * Returns the authenticated faculty's profile based on X-User-Id header
     * Auto-creates faculty record if not exists by fetching from Auth Service
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/profile - Fetching profile");

        String userIdHeader = request.getHeader("X-User-Id");
        log.info("Controller: User ID from header: {}", userIdHeader);

        if (userIdHeader == null) {
            log.error("Controller: X-User-Id header not found");
            Map<String, String> error = new HashMap<>();
            error.put("error", "User ID not found in request");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Integer facultyId = Integer.parseInt(userIdHeader);

            // First try to find existing faculty
            var existingFaculty = facultyService.getFacultyById(facultyId);
            if (existingFaculty.isPresent()) {
                log.info("Controller: Profile found - ID: {}, Email: {}", facultyId, existingFaculty.get().getEmail());
                log.info("===============================================");
                return ResponseEntity.ok(existingFaculty.get());
            }

            // Faculty not found locally - try to fetch from Auth Service and auto-create
            log.info("Controller: Faculty not found locally, fetching from Auth Service...");
            try {
                ResponseEntity<Map<String, Object>> authResponse = authInterface.getUserById(Long.valueOf(facultyId));

                if (authResponse.getStatusCode().is2xxSuccessful() && authResponse.getBody() != null) {
                    Map<String, Object> userData = authResponse.getBody();

                    // Create new faculty from auth user data
                    Faculty newFaculty = new Faculty();
                    newFaculty.setFId(facultyId);
                    newFaculty.setEmail((String) userData.get("email"));
                    newFaculty.setName((String) userData.get("name"));
                    newFaculty.setBio((String) userData.get("bio"));
                    newFaculty.setGithubProfileLink((String) userData.get("githubProfileLink"));
                    newFaculty.setLinkedInProfileLink((String) userData.get("linkedInProfileLink"));
                    newFaculty.setPortfolioLink((String) userData.get("portfolioLink"));
                    newFaculty.setPhone((String) userData.get("phone"));
                    newFaculty.setLocation((String) userData.get("location"));

                    // Handle numeric fields with null checks
                    Object ratings = userData.get("ratings");
                    newFaculty.setRatings(ratings != null ? ((Number) ratings).doubleValue() : 0.0);

                    Object projectsCompleted = userData.get("projectsCompleted");
                    newFaculty.setProjectsCompleted(
                            projectsCompleted != null ? ((Number) projectsCompleted).intValue() : 0);

                    Object currentProjects = userData.get("currentProjects");
                    newFaculty.setCurrentProjects(currentProjects != null ? ((Number) currentProjects).intValue() : 0);

                    // Save the new faculty
                    Faculty savedFaculty = facultyService.createFaculty(newFaculty);
                    log.info("Controller: Auto-created faculty profile - ID: {}, Email: {}", savedFaculty.getFId(),
                            savedFaculty.getEmail());
                    log.info("===============================================");
                    return ResponseEntity.ok(savedFaculty);
                } else {
                    log.warn("Controller: User not found in Auth Service for ID: {}", facultyId);
                    log.info("===============================================");
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception authEx) {
                log.error("Controller: Failed to fetch user from Auth Service: {}", authEx.getMessage());
                log.info("===============================================");
                return ResponseEntity.notFound().build();
            }
        } catch (NumberFormatException e) {
            log.error("Controller: Invalid user ID format: {}", userIdHeader);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid user ID format");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculty(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /api/faculty - Fetching all faculty");
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            List<Faculty> facultyList = facultyService.getAllFaculty();
            log.info("Controller: Successfully fetched {} faculty members", facultyList.size());
            log.info("===============================================");
            return ResponseEntity.ok(facultyList);
        } catch (Exception e) {
            log.error("Controller: Error fetching all faculty: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Integer id, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /api/faculty/{} - Fetching faculty by ID", id);
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            return facultyService.getFacultyById(id)
                    .map(faculty -> {
                        log.info("Controller: Faculty found - ID: {}, Email: {}, Name: {}",
                                id, faculty.getEmail(), faculty.getName());
                        log.info("===============================================");
                        return ResponseEntity.ok(faculty);
                    })
                    .orElseGet(() -> {
                        log.warn("Controller: Faculty not found with ID: {}", id);
                        log.info("===============================================");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Controller: Error fetching faculty by ID {}: {}", id, e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Faculty> getFacultyByEmail(@PathVariable String email, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /api/faculty/email/{} - Fetching faculty by email", email);
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            Faculty faculty = facultyService.getFacultyByEmail(email);
            log.info("Controller: Faculty found - Email: {}, ID: {}, Name: {}",
                    faculty.getEmail(), faculty.getFId(), faculty.getName());
            log.info("===============================================");
            return ResponseEntity.ok(faculty);
        } catch (RuntimeException e) {
            log.error("Controller: Faculty not found with email: {}", email);
            log.info("===============================================");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@Valid @RequestBody Faculty faculty, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: POST /api/faculty - Creating new faculty: {}", faculty.getEmail());
        log.debug("Controller: Faculty details - Name: {}, Email: {}, Department: {}",
                faculty.getName(), faculty.getEmail(), faculty.getDepartment());
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            Faculty createdFaculty = facultyService.createFaculty(faculty);
            log.info("Controller: Faculty created successfully - ID: {}, Email: {}",
                    createdFaculty.getFId(), createdFaculty.getEmail());
            log.info("===============================================");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
        } catch (RuntimeException e) {
            log.error("Controller: Error creating faculty {}: {}", faculty.getEmail(), e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(
            @PathVariable Integer id,
            @Valid @RequestBody Faculty facultyDetails,
            HttpServletRequest request) {

        log.info("===============================================");
        log.info("Controller: PUT /api/faculty/{} - Updating faculty", id);
        log.debug("Controller: Update details - Name: {}, Email: {}, Department: {}",
                facultyDetails.getName(), facultyDetails.getEmail(), facultyDetails.getDepartment());
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            Faculty updatedFaculty = facultyService.updateFaculty(id, facultyDetails);
            log.info("Controller: Faculty updated successfully - ID: {}, Email: {}",
                    updatedFaculty.getFId(), updatedFaculty.getEmail());
            log.info("===============================================");
            return ResponseEntity.ok(updatedFaculty);
        } catch (RuntimeException e) {
            log.error("Controller: Error updating faculty ID {}: {}", id, e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Integer id, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: DELETE /api/faculty/{} - Deleting faculty", id);
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            facultyService.deleteFaculty(id);
            log.info("Controller: Faculty deleted successfully - ID: {}", id);
            log.info("===============================================");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Controller: Error deleting faculty ID {}: {}", id, e.getMessage(), e);
            log.info("===============================================");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalFacultyCount(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /api/faculty/count - Getting total faculty count");
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            Integer count = facultyService.getTotalFacultyCount();
            log.info("Controller: Total faculty count: {}", count);
            log.info("===============================================");
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Controller: Error getting faculty count: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    @GetMapping("/emails")
    public ResponseEntity<List<String>> getAllFacultyEmails(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /api/faculty/emails - Getting all faculty emails");
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            List<String> emails = facultyService.getAllFacultyEmails();
            log.info("Controller: Fetched {} faculty emails", emails.size());
            log.debug("Controller: Emails: {}", emails);
            log.info("===============================================");
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Controller: Error fetching faculty emails: {}", e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkFacultyExists(@PathVariable String email, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /api/faculty/exists/{} - Checking if faculty exists", email);
        log.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            boolean exists = facultyService.existsByEmail(email);
            log.info("Controller: Faculty with email {} exists: {}", email, exists);
            log.info("===============================================");
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Controller: Error checking faculty existence for {}: {}", email, e.getMessage(), e);
            log.info("===============================================");
            throw e;
        }
    }
}
