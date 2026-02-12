package com.example.studentservice.controller;

import com.example.studentservice.client.dto.Status;
import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.repository.StudentProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/studentProject")
@RequiredArgsConstructor
@Slf4j
public class StudentProjectController {

    private final StudentProjectRepository studentProjectRepository;

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
}
