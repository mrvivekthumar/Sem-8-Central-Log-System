package com.example.studentservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentservice.domain.Student;
import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.security.CurrentUser;
import com.example.studentservice.service.StudentProjectService;

@RestController
@RequestMapping("/students/projects")
public class StudentProjectController {

    @Autowired
    private StudentProjectService studentProjectService;

    @Autowired
    private CurrentUser currentUser;

    /*
     * =========================
     * PROJECT → STUDENTS (READ)
     * =========================
     */

    @GetMapping("/{projectId}/students")
    public ResponseEntity<List<Student>> getStudentsByProject(@PathVariable int projectId) {
        return studentProjectService.getStudentIds(projectId);
    }

    @GetMapping("/{projectId}/student-count")
    public ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId) {
        return studentProjectService.getStudentCountByProjectId(projectId);
    }

    @GetMapping("/{projectId}/student-projects")
    public ResponseEntity<List<StudentProject>> getAllStudentsByProject(@PathVariable int projectId) {
        return studentProjectService.getAllStudentsBYProj(projectId);
    }

    /*
     * =========================
     * STUDENT-SCOPED ACTIONS
     * =========================
     */

    @PutMapping("/{projectId}/status")
    public ResponseEntity<String> updateStatus(@PathVariable int projectId) {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.updateStatus(studentId, projectId);
    }

    @GetMapping("/{projectId}/status")
    public ResponseEntity<Boolean> checkMyApplicationStatus(@PathVariable int projectId) {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.checkApplicationStatus(studentId, projectId);
    }

    @GetMapping("/me/applied-projects")
    public ResponseEntity<List<Integer>> getMyAppliedProjects() {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.getApliedProjectsIds(studentId);
    }

    @GetMapping("/me/approved-project")
    public ResponseEntity<Integer> getMyApprovedProject() {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.getApprovedProject(studentId);
    }

    @PatchMapping("/{projectId}/preference/{newPreference}")
    public ResponseEntity<String> updateProjectPreference(
            @PathVariable int projectId,
            @PathVariable int newPreference) {

        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.updateProjectPrefernce(studentId, projectId, newPreference);
    }

    @GetMapping("/me/project-preferences")
    public ResponseEntity<List<Integer>> getProjectIdsByPreference() {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.getProjIdsByPref(studentId);
    }

    @GetMapping("/{projectId}/me")
    public ResponseEntity<StudentProject> getMyStudentProject(
            @PathVariable int projectId) {

        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.findProjectByProjIdAndStudId(projectId, studentId);
    }

    /*
     * =========================
     * FACULTY / INTERNAL
     * =========================
     */

    @PostMapping("/{projectId}/students")
    public ResponseEntity<List<StudentProject>> getStudentProjectsByIds(
            @PathVariable int projectId,
            @RequestBody List<Integer> studentIds) {

        return studentProjectService.findProjectByProjIdAndStudIds(projectId, studentIds);
    }

    @GetMapping("/{projectId}/approved-students")
    public ResponseEntity<List<Integer>> getApprovedStudentsByProject(@PathVariable int projectId) {
        return studentProjectService.getApprovedStudentsByProjectId(projectId);
    }

    @GetMapping("/me/pending-approvals")
    public ResponseEntity<List<Integer>> getMyPendingApprovals() {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.getStudentPendingApprovals(studentId);
    }

    @GetMapping("/{projectId}/faculties")
    public ResponseEntity<List<Integer>> getFacultiesByProject(@PathVariable int projectId) {
        int studentId = currentUser.getUserId().intValue();
        return studentProjectService.getFacultiesByProject(studentId, projectId);
    }

    @GetMapping("/{projectId}/completed-students")
    public ResponseEntity<List<Integer>> getCompletedStudentIds(@PathVariable int projectId) {
        return studentProjectService.findCompletedStudentIds(projectId);
    }
}
