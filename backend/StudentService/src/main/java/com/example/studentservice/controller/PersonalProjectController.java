package com.example.studentservice.controller;

import com.example.studentservice.domain.PersonalProject;
import com.example.studentservice.domain.Student;
import com.example.studentservice.repository.PersonalProjectRepository;
import com.example.studentservice.repository.StudentRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/personalProject")
@Slf4j
public class PersonalProjectController {

    @Autowired
    private PersonalProjectRepository personalProjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * GET /student/personalProject/{studentId}
     * Get all personal projects for a student
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<List<PersonalProject>> getPersonalProjects(@PathVariable int studentId) {
        log.info("GET /personalProject/{}", studentId);
        List<PersonalProject> projects = personalProjectRepository.findByStudent_StudentId(studentId);
        return ResponseEntity.ok(projects);
    }

    /**
     * POST /student/personalProject/{studentId}
     * Create a personal project for a student
     */
    @PostMapping("/{studentId}")
    public ResponseEntity<?> createPersonalProject(
            @PathVariable int studentId,
            @RequestBody PersonalProject project) {
        log.info("POST /personalProject/{}", studentId);

        Optional<Student> optStudent = studentRepository.findById(studentId);
        if (optStudent.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
        }

        project.setStudent(optStudent.get());
        PersonalProject saved = personalProjectRepository.save(project);
        log.info("Personal project created with ID: {}", saved.getPersonalProjectId());
        return ResponseEntity.ok(saved);
    }

    /**
     * PUT /student/personalProject/{projectId}
     * Update a personal project
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<?> updatePersonalProject(
            @PathVariable int projectId,
            @RequestBody PersonalProject projectData) {
        log.info("PUT /personalProject/{}", projectId);

        Optional<PersonalProject> optProject = personalProjectRepository.findById(projectId);
        if (optProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PersonalProject existing = optProject.get();
        if (projectData.getName() != null) {
            existing.setName(projectData.getName());
        }
        if (projectData.getDescreption() != null) {
            existing.setDescreption(projectData.getDescreption());
        }
        if (projectData.getProjectLink() != null) {
            existing.setProjectLink(projectData.getProjectLink());
        }

        PersonalProject saved = personalProjectRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    /**
     * DELETE /student/personalProject/{projectId}
     * Delete a personal project
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deletePersonalProject(@PathVariable int projectId) {
        log.info("DELETE /personalProject/{}", projectId);
        if (!personalProjectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }
        personalProjectRepository.deleteById(projectId);
        return ResponseEntity.ok(Map.of("message", "Personal project deleted successfully"));
    }
}
