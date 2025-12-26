package com.example.facultyservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.facultyservice.domain.Faculty;
import com.example.facultyservice.service.FacultyService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/faculty")
@CrossOrigin(origins = "*")
@Slf4j
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @PostConstruct
    public void init() {
        log.info("=================================================");
        log.info("FacultyController initialized and ready!");
        log.info("Endpoints: GET/POST/PUT/DELETE /faculty");
        log.info("=================================================");
    }

    /**
     * Get all faculty members
     * GET /faculty
     */
    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculty(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty - Fetching all faculty");
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

    /**
     * Get faculty by ID
     * GET /faculty/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Integer id, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/{} - Fetching faculty by ID", id);
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

    /**
     * Get faculty by email
     * GET /faculty/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Faculty> getFacultyByEmail(@PathVariable String email, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/email/{} - Fetching faculty by email", email);
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

    /**
     * Create new faculty
     * POST /faculty
     */
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: POST /faculty - Creating new faculty: {}", faculty.getEmail());
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

    /**
     * Update faculty profile
     * PUT /faculty/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(
            @PathVariable Integer id,
            @RequestBody Faculty facultyDetails,
            HttpServletRequest request) {

        log.info("===============================================");
        log.info("Controller: PUT /faculty/{} - Updating faculty", id);
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

    /**
     * Delete faculty
     * DELETE /faculty/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Integer id, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: DELETE /faculty/{} - Deleting faculty", id);
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

    /**
     * Get total faculty count
     * GET /faculty/count
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalFacultyCount(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/count - Getting total faculty count");
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

    /**
     * Get all faculty emails
     * GET /faculty/emails
     */
    @GetMapping("/emails")
    public ResponseEntity<List<String>> getAllFacultyEmails(HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/emails - Getting all faculty emails");
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

    /**
     * Check if faculty exists by email
     * GET /faculty/exists/{email}
     */
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkFacultyExists(@PathVariable String email, HttpServletRequest request) {
        log.info("===============================================");
        log.info("Controller: GET /faculty/exists/{} - Checking if faculty exists", email);
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
