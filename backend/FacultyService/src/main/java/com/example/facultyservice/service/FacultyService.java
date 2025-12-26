package com.example.facultyservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.facultyservice.domain.Faculty;
import com.example.facultyservice.repository.FacultyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    /**
     * Get all faculty members
     */
    public List<Faculty> getAllFaculty() {
        log.info("Fetching all faculty");
        return facultyRepository.findAll();
    }

    /**
     * Get faculty by ID
     */
    public Optional<Faculty> getFacultyById(Integer id) {
        log.info("Fetching faculty by ID: {}", id);
        return facultyRepository.findById(id);
    }

    /**
     * Get faculty by email
     */
    public Faculty getFacultyByEmail(String email) {
        log.info("Fetching faculty by email: {}", email);
        Faculty faculty = facultyRepository.findByEmail(email);

        if (faculty == null) {
            log.error("Faculty not found with email: {}", email);
            throw new RuntimeException("Faculty not found with email: " + email);
        }

        return faculty;
    }

    /**
     * Create new faculty
     */
    public Faculty createFaculty(Faculty faculty) {
        log.info("Creating new faculty: {}", faculty.getEmail());

        if (facultyRepository.existsByEmail(faculty.getEmail())) {
            throw new RuntimeException("Faculty already exists with email: " + faculty.getEmail());
        }

        return facultyRepository.save(faculty);
    }

    /**
     * Update faculty profile
     */
    public Faculty updateFaculty(Integer id, Faculty facultyDetails) {
        log.info("Updating faculty with ID: {}", id);

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));

        // Update fields (excluding password and email for security)
        if (facultyDetails.getName() != null) {
            faculty.setName(facultyDetails.getName());
        }
        if (facultyDetails.getDepartment() != null) {
            faculty.setDepartment(facultyDetails.getDepartment());
        }

        Faculty updatedFaculty = facultyRepository.save(faculty);
        log.info("Faculty updated successfully: {}", id);

        return updatedFaculty;
    }

    /**
     * Delete faculty
     */
    public void deleteFaculty(Integer id) {
        log.info("Deleting faculty with ID: {}", id);

        if (!facultyRepository.existsById(id)) {
            throw new RuntimeException("Faculty not found with id: " + id);
        }

        facultyRepository.deleteById(id);
        log.info("Faculty deleted successfully: {}", id);
    }

    /**
     * Get total faculty count
     */
    public Integer getTotalFacultyCount() {
        return facultyRepository.findTotalUsers();
    }

    /**
     * Get all faculty emails
     */
    public List<String> getAllFacultyEmails() {
        return facultyRepository.findAllEmails();
    }

    /**
     * Check if faculty exists by email
     */
    public boolean existsByEmail(String email) {
        return facultyRepository.existsByEmail(email);
    }
}
