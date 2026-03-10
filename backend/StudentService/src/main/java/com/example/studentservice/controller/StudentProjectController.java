package com.example.studentservice.controller;

import com.example.studentservice.client.AuthInterface;
import com.example.studentservice.client.FacultyInterface;
import com.example.studentservice.client.dto.Project;
import com.example.studentservice.client.dto.Status;
import com.example.studentservice.domain.Student;
import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.repository.StudentProjectRepository;
import com.example.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/studentProject")
@RequiredArgsConstructor
@Slf4j
public class StudentProjectController {

    private final StudentProjectRepository studentProjectRepository;
    private final StudentRepository studentRepository;
    private final FacultyInterface facultyInterface;
    private final AuthInterface authInterface;

    /**
     * Get applied project IDs ordered by preference for a student
     * GET /student/studentProject/projectIdsByPref/{studentId}
     */
    @GetMapping("/projectIdsByPref/{studentId}")
    public ResponseEntity<List<Integer>> getProjectIdsByPreference(@PathVariable int studentId) {
        log.info("Controller: GET /studentProject/projectIdsByPref/{} - Fetching project IDs", studentId);
        List<Integer> projectIds = studentProjectRepository.findProjectIdsByStudentOrdered(studentId);
        log.info("Controller: Found {} project IDs for student {}", projectIds.size(), studentId);
        return ResponseEntity.ok(projectIds);
    }

    /**
     * Get all applied projects for current user
     * GET /student/studentProject/appliedProjects
     */
    @GetMapping("/appliedProjects")
    public ResponseEntity<List<StudentProject>> getAppliedProjects(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("Controller: GET /studentProject/appliedProjects - User: {}", userId);

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        int studentId = Integer.parseInt(userId);
        List<StudentProject> projects = studentProjectRepository.findProjectsByStudentOrdered(studentId);
        log.info("Controller: Found {} applied projects for student {}", projects.size(), studentId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get student-project status
     * GET /student/studentProject/student/{studentId}/project/{projectId}
     */
    @GetMapping("/student/{studentId}/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getStudentProjectStatus(
            @PathVariable int studentId,
            @PathVariable int projectId) {
        log.info("Controller: GET /studentProject/student/{}/project/{}", studentId, projectId);

        StudentProject sp = studentProjectRepository.findByStudent_StudentIdAndProjectId(studentId, projectId);

        if (sp == null) {
            Map<String, Object> notFound = new HashMap<>();
            notFound.put("applied", false);
            notFound.put("status", null);
            return ResponseEntity.ok(notFound);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("applied", true);
        response.put("applicationId", sp.getApplicationId());
        response.put("status", sp.getStatus());
        response.put("preference", sp.getPreference());
        response.put("applicationDate", sp.getApplicationDate());

        return ResponseEntity.ok(response);
    }

    /**
     * Apply to a project (create student-project application)
     * POST /student/studentProject/student/{studentId}/project/{projectId}
     */
    @PostMapping("/student/{studentId}/project/{projectId}")
    public ResponseEntity<Map<String, Object>> applyToProject(
            @PathVariable int studentId,
            @PathVariable int projectId) {
        log.info("Controller: POST /studentProject/student/{}/project/{} - Applying", studentId, projectId);

        // Check if already applied
        StudentProject existing = studentProjectRepository.findByStudent_StudentIdAndProjectId(studentId, projectId);
        if (existing != null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Already applied to this project");
            return ResponseEntity.badRequest().body(error);
        }

        // Get or create Student entity (auto-register from Auth Service if needed)
        Student student;
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isPresent()) {
            student = studentOpt.get();
        } else {
            log.info("Student not found locally, fetching from Auth Service: {}", studentId);
            try {
                ResponseEntity<Map<String, Object>> authResponse = authInterface.getUserById(Long.valueOf(studentId));
                if (authResponse.getStatusCode().is2xxSuccessful() && authResponse.getBody() != null) {
                    Map<String, Object> userData = authResponse.getBody();
                    Student newStudent = new Student();
                    newStudent.setStudentId(studentId);
                    newStudent.setEmail((String) userData.get("email"));
                    newStudent.setName((String) userData.get("name"));
                    newStudent.setPhone((String) userData.get("phone"));
                    student = studentRepository.save(newStudent);
                    log.info("Auto-created student - ID: {}, Email: {}", student.getStudentId(), student.getEmail());
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "Student not found");
                    return ResponseEntity.badRequest().body(error);
                }
            } catch (Exception e) {
                log.error("Failed to fetch student from Auth Service: {}", e.getMessage());
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Student not found");
                return ResponseEntity.badRequest().body(error);
            }
        }

        // Get project name from Faculty Service
        String projectName = "Project " + projectId;
        try {
            ResponseEntity<Project> projectResp = facultyInterface.getProjectById(projectId);
            if (projectResp.getBody() != null) {
                projectName = projectResp.getBody().getTitle();
            }
        } catch (Exception e) {
            log.warn("Could not fetch project name from Faculty Service: {}", e.getMessage());
        }

        // Calculate next preference
        Integer maxPref = studentProjectRepository.findMaxPreferenceByStudentId(studentId);
        int nextPref = (maxPref != null) ? maxPref + 1 : 1;

        // Create application
        StudentProject sp = new StudentProject();
        sp.setStudent(student);
        sp.setProjectId(projectId);
        sp.setProjectName(projectName);
        sp.setStatus(Status.PENDING);
        sp.setApplicationDate(LocalDate.now());
        sp.setPreference(nextPref);

        StudentProject saved = studentProjectRepository.save(sp);
        log.info("Controller: Application created with ID {} for student {} project {}", saved.getApplicationId(),
                studentId, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("applied", true);
        response.put("applicationId", saved.getApplicationId());
        response.put("status", saved.getStatus());
        response.put("preference", saved.getPreference());
        response.put("applicationDate", saved.getApplicationDate());

        return ResponseEntity.ok(response);
    }

    /**
     * Get students who applied for a project
     * GET /student/studentProject/students/{projectId}
     */
    @GetMapping("/students/{projectId}")
    public ResponseEntity<List<StudentProject>> getStudentsByProject(@PathVariable int projectId) {
        log.info("Controller: GET /studentProject/students/{}", projectId);
        List<StudentProject> applications = studentProjectRepository.findByProjectId(projectId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Get student count for a project
     * GET /student/studentProject/{projectId}/student-count
     */
    @GetMapping("/{projectId}/student-count")
    public ResponseEntity<Integer> getStudentCount(@PathVariable int projectId) {
        log.info("Controller: GET /studentProject/{}/student-count", projectId);
        int count = studentProjectRepository.countStudentsByProjectId(projectId);
        return ResponseEntity.ok(count);
    }

    /**
     * Update preference for a student-project
     * PUT
     * /student/studentProject/updatePreference/{studentId}/project/{projectId}/{preference}
     */
    @PutMapping("/updatePreference/{studentId}/project/{projectId}/{preference}")
    public ResponseEntity<StudentProject> updatePreference(
            @PathVariable int studentId,
            @PathVariable int projectId,
            @PathVariable int preference) {
        log.info("Controller: PUT /studentProject/updatePreference/{}/project/{}/{}", studentId, projectId, preference);

        StudentProject sp = studentProjectRepository.findByStudent_StudentIdAndProjectId(studentId, projectId);

        if (sp == null) {
            log.warn("Controller: StudentProject not found for student {} and project {}", studentId, projectId);
            return ResponseEntity.notFound().build();
        }

        sp.setPreference(preference);
        StudentProject updated = studentProjectRepository.save(sp);
        log.info("Controller: Updated preference to {} for student {} project {}", preference, studentId, projectId);

        return ResponseEntity.ok(updated);
    }

    /**
     * Update status for a student-project
     * PUT /student/studentProject/updateStatus/{studentId}/{projectId}
     */
    @PutMapping("/updateStatus/{studentId}/{projectId}")
    public ResponseEntity<StudentProject> updateStatus(
            @PathVariable int studentId,
            @PathVariable int projectId,
            @RequestBody Map<String, String> body) {
        log.info("Controller: PUT /studentProject/updateStatus/{}/{}", studentId, projectId);

        StudentProject sp = studentProjectRepository.findByStudent_StudentIdAndProjectId(studentId, projectId);

        if (sp == null) {
            log.warn("Controller: StudentProject not found");
            return ResponseEntity.notFound().build();
        }

        String statusStr = body.get("status");
        if (statusStr != null) {
            sp.setStatus(Status.valueOf(statusStr));
        }

        StudentProject updated = studentProjectRepository.save(sp);
        log.info("Controller: Updated status to {} for student {} project {}", statusStr, studentId, projectId);

        // Send notification when student is rejected
        if ("REJECTED".equals(statusStr)) {
            try {
                String projectName = sp.getProjectName() != null ? sp.getProjectName() : "Project " + projectId;
                Map<String, Object> notifRequest = new HashMap<>();
                notifRequest.put("senderId", String.valueOf(projectId));
                notifRequest.put("senderType", "FACULTY");
                notifRequest.put("receiverId", String.valueOf(studentId));
                notifRequest.put("receiverType", "STUDENT");
                notifRequest.put("notificationType", "PROJECT_ASSIGNMENT");
                notifRequest.put("title", "Application Update");
                notifRequest.put("message", "Your application for \"" + projectName
                        + "\" was not selected. Don't worry, keep exploring other projects!");
                notifRequest.put("seen", false);

                facultyInterface.sendNotification(notifRequest);
                log.info("Controller: Rejection notification sent to student {}", studentId);
            } catch (Exception e) {
                log.error("Controller: Failed to send rejection notification to student {}: {}",
                        studentId, e.getMessage());
            }
        }

        return ResponseEntity.ok(updated);
    }

    /**
     * Get students by project (alternative endpoint)
     * GET /student/studentProject/{projectId}/student
     */
    @GetMapping("/{projectId}/student")
    public ResponseEntity<List<StudentProject>> getStudentsForProject(@PathVariable int projectId) {
        log.info("Controller: GET /studentProject/{}/student", projectId);
        List<StudentProject> applications = studentProjectRepository.findByProjectId(projectId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Update ratings for students in a project
     * PUT /student/studentProject/{projectId}/rating/{rating}
     * Called by FacultyService when faculty rates a project
     */
    @PutMapping("/{projectId}/rating/{rating}")
    public ResponseEntity<String> updateRatings(
            @PathVariable int projectId,
            @PathVariable float rating) {
        log.info("Controller: PUT /studentProject/{}/rating/{}", projectId, rating);

        try {
            List<StudentProject> studentProjects = studentProjectRepository.findByProjectId(projectId);
            for (StudentProject sp : studentProjects) {
                if (sp.getStudent() != null) {
                    // Update student rating (simple average)
                    float currentRating = sp.getStudent().getRatings();
                    int totalRatings = sp.getStudent().getTotalRatings();
                    float newAverage = ((currentRating * totalRatings) + rating) / (totalRatings + 1);
                    sp.getStudent().setRatings(newAverage);
                    sp.getStudent().setTotalRatings(totalRatings + 1);
                }
            }
            studentProjectRepository.saveAll(studentProjects);
            log.info("Controller: Updated ratings for {} students on project {}", studentProjects.size(), projectId);
            return ResponseEntity.ok("Ratings updated successfully");
        } catch (Exception e) {
            log.error("Controller: Failed to update ratings for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Failed to update ratings: " + e.getMessage());
        }
    }
}
