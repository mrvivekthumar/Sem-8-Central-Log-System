package com.example.studentservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.studentservice.client.dto.Project;
import com.example.studentservice.domain.PersonalProject;
import com.example.studentservice.domain.Student;
import com.example.studentservice.security.CurrentUser;
import com.example.studentservice.service.StudentProjectService;
import com.example.studentservice.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentProjectService studentProjectService;

    /*
     * =========================
     * REGISTRATION (PUBLIC / ADMIN)
     * =========================
     */

    @PostMapping("/register")
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.registerStudent(student));
    }

    @PostMapping(value = "/register-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(studentService.registerFile(file));
    }

    /*
     * =========================
     * PROJECT APPLICATION
     * =========================
     */

    @PostMapping("/projects/{projectId}/apply")
    public ResponseEntity<String> applyProject(@PathVariable int projectId) {
        int studentId = currentUser.getUserId().intValue();
        studentService.applyProject(studentId, projectId);
        return ResponseEntity.ok("Applied successfully");
    }

    @PostMapping("/projects/{projectId}/withdraw")
    public ResponseEntity<String> withdrawProject(@PathVariable int projectId) {
        int studentId = currentUser.getUserId().intValue();
        studentService.withdrawProject(studentId, projectId);
        return ResponseEntity.ok("Withdrawn successfully");
    }

    /*
     * =========================
     * STUDENT PROFILE
     * =========================
     */

    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudent(@PathVariable int studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    @PutMapping("/me/github")
    public ResponseEntity<Student> updateGithubProfile(@RequestParam String githubLink) {
        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(studentService.updateGithubLink(studentId, githubLink));
    }

    @PutMapping("/me")
    public ResponseEntity<Student> updateProfile(@RequestBody Student student) {
        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(studentService.updateStudentDetails(studentId, student));
    }

    /*
     * =========================
     * PERSONAL PROJECTS
     * =========================
     */

    @PostMapping("/me/projects")
    public ResponseEntity<PersonalProject> addPersonalProject(
            @RequestBody PersonalProject personalProject) {

        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.addPersonalProject(studentId, personalProject));
    }

    @DeleteMapping("/me/projects/{personalProjectId}")
    public ResponseEntity<String> deletePersonalProject(
            @PathVariable int personalProjectId) {

        int studentId = currentUser.getUserId().intValue();
        studentService.deletePersonalProject(studentId, personalProjectId);
        return ResponseEntity.ok("Personal project deleted successfully");
    }

    /*
     * =========================
     * AVAILABILITY & STATUS
     * =========================
     */

    @PutMapping("/me/unavailable")
    public ResponseEntity<String> makeUnavailable() {
        int studentId = currentUser.getUserId().intValue();
        studentService.makeUnavailable(studentId);
        return ResponseEntity.ok("Marked as unavailable");
    }

    @GetMapping("/me/completed-projects")
    public ResponseEntity<List<Integer>> getCompletedProjects() {
        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(studentService.getCompletedProjects(studentId));
    }

    /*
     * =========================
     * PROJECT DISCOVERY
     * =========================
     */

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(studentService.getAllProjects());
    }

    @GetMapping("/projects/visible")
    public ResponseEntity<List<Project>> getVisibleProjects() {
        return studentProjectService.getVisibleProjects();
    }

    @GetMapping("/projects/{projectId}/teammates")
    public ResponseEntity<List<Student>> getTeamMates(@PathVariable int projectId) {
        return studentProjectService.getTeamMates(projectId);
    }

    /*
     * =========================
     * MEDIA
     * =========================
     */

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Student> updateAvatar(
            @RequestParam("image") MultipartFile avatar) {

        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(studentService.uploadAvatar(studentId, avatar));
    }

    @PostMapping(value = "/me/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Student> uploadImage(
            @RequestParam("image") MultipartFile image) {

        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(studentService.uploadImageToCloudinary(image, studentId));
    }

    /*
     * =========================
     * FACULTY ACTIONS
     * =========================
     */

    @PutMapping("/projects/{projectId}/ratings/{ratings}")
    public ResponseEntity<String> updateScoresByFaculty(
            @PathVariable int projectId,
            @PathVariable float ratings) {

        studentService.updateScoreByFaculty(projectId, ratings);
        return ResponseEntity.ok("Ratings updated successfully");
    }

    /*
     * =========================
     * ADMIN / INTERNAL
     * =========================
     */

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount() {
        return ResponseEntity.ok(studentService.getCount());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email) {
        return ResponseEntity.ok(studentService.getStudentByEmail(email));
    }
}
