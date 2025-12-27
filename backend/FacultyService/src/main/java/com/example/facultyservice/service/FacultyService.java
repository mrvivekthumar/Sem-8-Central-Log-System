package com.example.facultyservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.facultyservice.entity.Faculty;
import com.example.facultyservice.repository.FacultyRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @PostConstruct
    public void init() {
        log.info("FacultyService initialized and ready");
    }

    public List<Faculty> getAllFaculty() {
        log.info("Service: Fetching all faculty");

        try {
            List<Faculty> facultyList = facultyRepository.findAll();
            log.info("Service: Successfully fetched {} faculty members", facultyList.size());
            log.debug("Service: Faculty list: {}", facultyList);
            return facultyList;
        } catch (Exception e) {
            log.error("Service: Error fetching all faculty: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<Faculty> getFacultyById(Integer id) {
        log.info("Service: Fetching faculty by ID: {}", id);

        try {
            Optional<Faculty> faculty = facultyRepository.findById(id);
            if (faculty.isPresent()) {
                log.info("Service: Faculty found - ID: {}, Email: {}", id, faculty.get().getEmail());
            } else {
                log.warn("Service: Faculty not found with ID: {}", id);
            }
            return faculty;
        } catch (Exception e) {
            log.error("Service: Error fetching faculty by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public Faculty getFacultyByEmail(String email) {
        log.info("Service: Fetching faculty by email: {}", email);

        try {
            Faculty faculty = facultyRepository.findByEmail(email);

            if (faculty == null) {
                log.error("Service: Faculty not found with email: {}", email);
                throw new RuntimeException("Faculty not found with email: " + email);
            }

            log.info("Service: Faculty found - Email: {}, ID: {}, Name: {}",
                    faculty.getEmail(), faculty.getFId(), faculty.getName());
            return faculty;
        } catch (RuntimeException e) {
            log.error("Service: Error fetching faculty by email {}: {}", email, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service: Unexpected error fetching faculty by email {}: {}",
                    email, e.getMessage(), e);
            throw e;
        }
    }

    public Faculty createFaculty(Faculty faculty) {
        log.info("Service: Creating new faculty: {}", faculty.getEmail());
        log.debug("Service: Faculty details - Name: {}, Email: {}, Department: {}",
                faculty.getName(), faculty.getEmail(), faculty.getDepartment());

        try {
            if (facultyRepository.existsByEmail(faculty.getEmail())) {
                log.warn("Service: Faculty already exists with email: {}", faculty.getEmail());
                throw new RuntimeException("Faculty already exists with email: " + faculty.getEmail());
            }

            // ✅ Initialize default values
            if (faculty.getSkills() == null) {
                faculty.setSkills(new ArrayList<>());
            }
            if (faculty.getRatings() == null) {
                faculty.setRatings(0.0);
            }
            if (faculty.getProjectsCompleted() == null) {
                faculty.setProjectsCompleted(0);
            }
            if (faculty.getCurrentProjects() == null) {
                faculty.setCurrentProjects(0);
            }

            Faculty savedFaculty = facultyRepository.save(faculty);
            log.info("Service: Faculty created successfully - ID: {}, Email: {}",
                    savedFaculty.getFId(), savedFaculty.getEmail());
            return savedFaculty;
        } catch (RuntimeException e) {
            log.error("Service: Error creating faculty {}: {}", faculty.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service: Unexpected error creating faculty {}: {}",
                    faculty.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    public Faculty updateFaculty(Integer id, Faculty facultyDetails) {
        log.info("Service: Updating faculty with ID: {}", id);
        log.debug("Service: Update details - Name: {}, Department: {}",
                facultyDetails.getName(), facultyDetails.getDepartment());

        try {
            Faculty faculty = facultyRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Service: Faculty not found with ID: {}", id);
                        return new RuntimeException("Faculty not found with id: " + id);
                    });

            log.debug("Service: Current faculty details - Email: {}, Name: {}, Department: {}",
                    faculty.getEmail(), faculty.getName(), faculty.getDepartment());

            // ✅ Update ALL fields from frontend
            if (facultyDetails.getName() != null) {
                log.debug("Service: Updating name from '{}' to '{}'",
                        faculty.getName(), facultyDetails.getName());
                faculty.setName(facultyDetails.getName());
            }
            if (facultyDetails.getDepartment() != null) {
                log.debug("Service: Updating department from '{}' to '{}'",
                        faculty.getDepartment(), facultyDetails.getDepartment());
                faculty.setDepartment(facultyDetails.getDepartment());
            }
            if (facultyDetails.getEmail() != null && !facultyDetails.getEmail().equals(faculty.getEmail())) {
                if (facultyRepository.existsByEmail(facultyDetails.getEmail())) {
                    log.warn("Service: Email already in use: {}", facultyDetails.getEmail());
                    throw new RuntimeException("Email already in use: " + facultyDetails.getEmail());
                }
                faculty.setEmail(facultyDetails.getEmail());
            }

            // ✅ Update profile fields
            if (facultyDetails.getBio() != null) {
                faculty.setBio(facultyDetails.getBio());
            }
            if (facultyDetails.getSkills() != null) {
                faculty.setSkills(facultyDetails.getSkills());
            }
            if (facultyDetails.getGithubProfileLink() != null) {
                faculty.setGithubProfileLink(facultyDetails.getGithubProfileLink());
            }
            if (facultyDetails.getLinkedInProfileLink() != null) {
                faculty.setLinkedInProfileLink(facultyDetails.getLinkedInProfileLink());
            }
            if (facultyDetails.getPortfolioLink() != null) {
                faculty.setPortfolioLink(facultyDetails.getPortfolioLink());
            }
            if (facultyDetails.getPhone() != null) {
                faculty.setPhone(facultyDetails.getPhone());
            }
            if (facultyDetails.getLocation() != null) {
                faculty.setLocation(facultyDetails.getLocation());
            }

            // ✅ Update stats if provided
            if (facultyDetails.getRatings() != null) {
                faculty.setRatings(facultyDetails.getRatings());
            }
            if (facultyDetails.getProjectsCompleted() != null) {
                faculty.setProjectsCompleted(facultyDetails.getProjectsCompleted());
            }
            if (facultyDetails.getCurrentProjects() != null) {
                faculty.setCurrentProjects(facultyDetails.getCurrentProjects());
            }

            Faculty updatedFaculty = facultyRepository.save(faculty);
            log.info("Service: Faculty updated successfully - ID: {}, Email: {}",
                    updatedFaculty.getFId(), updatedFaculty.getEmail());

            return updatedFaculty;
        } catch (RuntimeException e) {
            log.error("Service: Error updating faculty ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service: Unexpected error updating faculty ID {}: {}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteFaculty(Integer id) {
        log.info("Service: Deleting faculty with ID: {}", id);

        try {
            if (!facultyRepository.existsById(id)) {
                log.error("Service: Faculty not found with ID: {}", id);
                throw new RuntimeException("Faculty not found with id: " + id);
            }

            facultyRepository.deleteById(id);
            log.info("Service: Faculty deleted successfully - ID: {}", id);
        } catch (RuntimeException e) {
            log.error("Service: Error deleting faculty ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service: Unexpected error deleting faculty ID {}: {}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    public Integer getTotalFacultyCount() {
        log.info("Service: Getting total faculty count");

        try {
            Integer count = facultyRepository.findTotalUsers();
            log.info("Service: Total faculty count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Service: Error getting faculty count: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<String> getAllFacultyEmails() {
        log.info("Service: Getting all faculty emails");

        try {
            List<String> emails = facultyRepository.findAllEmails();
            log.info("Service: Fetched {} faculty emails", emails.size());
            log.debug("Service: Faculty emails: {}", emails);
            return emails;
        } catch (Exception e) {
            log.error("Service: Error fetching faculty emails: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean existsByEmail(String email) {
        log.debug("Service: Checking if faculty exists with email: {}", email);

        try {
            boolean exists = facultyRepository.existsByEmail(email);
            log.debug("Service: Faculty with email {} exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            log.error("Service: Error checking faculty existence for email {}: {}",
                    email, e.getMessage(), e);
            throw e;
        }
    }
}
