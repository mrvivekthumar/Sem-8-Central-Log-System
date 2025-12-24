package com.example.studentservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentservice.client.dto.PersonalProjectDTO;
import com.example.studentservice.security.CurrentUser;
import com.example.studentservice.service.PersonalProjectService;

@RestController
@RequestMapping("/students/personal-projects")
public class PersonalProjectController {

    @Autowired
    private PersonalProjectService personalProjectService;

    @Autowired
    private CurrentUser currentUser;

    /*
     * =========================
     * CREATE
     * =========================
     */
    @PostMapping
    public ResponseEntity<PersonalProjectDTO> createPersonalProject(
            @RequestBody PersonalProjectDTO personalProjectDTO) {

        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(
                personalProjectService.createPersonalProject(personalProjectDTO, studentId));
    }

    /*
     * =========================
     * READ
     * =========================
     */
    @GetMapping
    public ResponseEntity<List<PersonalProjectDTO>> getMyPersonalProjects() {

        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(
                personalProjectService.getAllPersonalProjects(studentId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<PersonalProjectDTO> getPersonalProjectById(
            @PathVariable int projectId) {

        return ResponseEntity.ok(
                personalProjectService.getPersonalProjectById(projectId));
    }

    /*
     * =========================
     * UPDATE
     * =========================
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<PersonalProjectDTO> updatePersonalProject(
            @PathVariable int projectId,
            @RequestBody PersonalProjectDTO personalProjectDTO) {

        return ResponseEntity.ok(
                personalProjectService.updatePersonalProject(projectId, personalProjectDTO));
    }

    /*
     * =========================
     * DELETE
     * =========================
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deletePersonalProject(@PathVariable int projectId) {

        personalProjectService.deletePersonalProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
