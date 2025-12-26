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

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/faculty")
@CrossOrigin(origins = "*")
@Slf4j
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    /**
     * Get all faculty members
     * GET /faculty
     */
    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculty() {
        log.info("GET /faculty - Fetching all faculty");
        List<Faculty> facultyList = facultyService.getAllFaculty();
        return ResponseEntity.ok(facultyList);
    }

    /**
     * Get faculty by ID
     * GET /faculty/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Integer id) {
        log.info("GET /faculty/{} - Fetching faculty by ID", id);

        return facultyService.getFacultyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get faculty by email
     * GET /faculty/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Faculty> getFacultyByEmail(@PathVariable String email) {
        log.info("GET /faculty/email/{} - Fetching faculty by email", email);

        try {
            Faculty faculty = facultyService.getFacultyByEmail(email);
            return ResponseEntity.ok(faculty);
        } catch (RuntimeException e) {
            log.error("Faculty not found with email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new faculty
     * POST /faculty
     */
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        log.info("POST /faculty - Creating new faculty: {}", faculty.getEmail());

        try {
            Faculty createdFaculty = facultyService.createFaculty(faculty);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
        } catch (RuntimeException e) {
            log.error("Error creating faculty: {}", e.getMessage());
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
            @RequestBody Faculty facultyDetails) {

        log.info("PUT /faculty/{} - Updating faculty", id);

        try {
            Faculty updatedFaculty = facultyService.updateFaculty(id, facultyDetails);
            return ResponseEntity.ok(updatedFaculty);
        } catch (RuntimeException e) {
            log.error("Error updating faculty: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete faculty
     * DELETE /faculty/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Integer id) {
        log.info("DELETE /faculty/{} - Deleting faculty", id);

        try {
            facultyService.deleteFaculty(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting faculty: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get total faculty count
     * GET /faculty/count
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalFacultyCount() {
        log.info("GET /faculty/count - Getting total faculty count");
        Integer count = facultyService.getTotalFacultyCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get all faculty emails
     * GET /faculty/emails
     */
    @GetMapping("/emails")
    public ResponseEntity<List<String>> getAllFacultyEmails() {
        log.info("GET /faculty/emails - Getting all faculty emails");
        List<String> emails = facultyService.getAllFacultyEmails();
        return ResponseEntity.ok(emails);
    }

    /**
     * Check if faculty exists by email
     * GET /faculty/exists/{email}
     */
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkFacultyExists(@PathVariable String email) {
        log.info("GET /faculty/exists/{} - Checking if faculty exists", email);
        boolean exists = facultyService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
