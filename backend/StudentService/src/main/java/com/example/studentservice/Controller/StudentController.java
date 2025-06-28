package com.example.studentservice.Controller;

import com.example.studentservice.Dao.StudentDao;
import com.example.studentservice.Model.PersonalProject;
import com.example.studentservice.Service.StudentProjectService;
import com.example.studentservice.Vo.Project;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Service.StudentService;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.netflix.eureka.http.RestClientEurekaHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("students")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentProjectService studentProjectService;
    @Autowired
    private StudentDao studentDao;

    @PostMapping("register")
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        System.out.println("Gonna register controller");
        return studentService.registerStudent(student);
    }
    @PostMapping(value = "/registerFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerFile(@RequestParam("file") MultipartFile file){
        return studentService.registerFile(file);
    }
    @PostMapping("apply/{studentId}/project/{projectId}")
    public ResponseEntity<String> applyProject(@PathVariable int studentId,@PathVariable int projectId){
        return studentService.applyProject(studentId,projectId);

    }
    @PostMapping("withdraw/{studentId}/project/{projectId}")
    public ResponseEntity<String> withdrawProject(@PathVariable int studentId,@PathVariable int projectId){
        return studentService.withdrawProject(studentId,projectId);
    }
    @GetMapping("projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return studentService.getAllProjets();
    }
    @PutMapping("{studentId}")
    public ResponseEntity<String> makeUnavailable(@PathVariable int studentId){
        return studentService.makeUnavailibity(studentId);
    }
    @GetMapping("project/visible")
    public ResponseEntity<List<Project>> getVisibleProjects(){
        return studentProjectService.getVisibleProjects();
    }
    @GetMapping("{studentId}")
    public ResponseEntity<Student> getStudent(@PathVariable int studentId){
        return studentService.getStudentById(studentId);
    }
    // Update GitHub profile link
    @PutMapping("/{studentId}/updateGithub")
    public ResponseEntity<Student> updateGithubProfile(@PathVariable int studentId, @RequestParam String githubLink) {
        return studentService.updateGithubLink(studentId,githubLink);
    }
    @PostMapping("{studentId}/project")
    public ResponseEntity<PersonalProject> addProject(@PathVariable int studentId,@RequestBody PersonalProject personalProject){
        return studentService.addProject(studentId,personalProject);

    }
    @DeleteMapping("{studentId}/project/{personalProjectId}")
    public ResponseEntity<Student> deleteProject(@PathVariable int studentId,@PathVariable int personalProjectId){
        return studentService.deleteProject(studentId,personalProjectId);
    }
    @GetMapping("by-id")
    public ResponseEntity<List<Student>> getStudentsById(@RequestParam List<Integer> ids){
        System.out.println("Fuck You");
        return studentService.getAllStudentsById(ids);

    }
    @GetMapping("ids")
    public ResponseEntity<List<Student>>getStudentsByIds(@RequestBody List<Integer>ids){
        return studentService.getAllStudentsById(ids);
    }
    @PutMapping("available/{projectId}")
    public ResponseEntity<String> updateStudentsAvailable(@PathVariable int projectId){
        System.out.println("Hy Path" );
        return studentService.updateStudentsAvailable(projectId);

    }
    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(){
        return studentService.getCount();

    }
    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email){
        return studentService.findByEmail(email);
    }
    @PutMapping("/student/{studentId}")
    public ResponseEntity<Student> updateDetails(@PathVariable int studentId,@RequestBody Student student){

        return studentService.updateStudentDetails(studentId,student);

    }
    @GetMapping("all")
    public ResponseEntity<List<Student>> getAllFaculties(){
        return studentService.findAll();
    }
    @PutMapping("/student/{studentId}/avtar")
    public ResponseEntity<Student> updateDetails(
            @PathVariable int studentId,
            @RequestParam("image") MultipartFile avtar){

        return studentService.editAvtar(studentId,avtar);
    }
    @PostMapping(value = "student/{studentId}/upload-image")
    public ResponseEntity<Student> uploadImage(@RequestParam("image") MultipartFile image,@PathVariable int studentId) {
        return studentService.uploadImageToCloudinary(image,studentId);
    }
    @GetMapping("/projects/{projectId}/teammates")
    public ResponseEntity<List<Student>> getAllTeamMates(@PathVariable int projectId){
        return studentProjectService.getTeamMates(projectId);
    }
    @PutMapping("/projects/{projectId}/ratings/{ratings}")
    public ResponseEntity<String> updateScoresByFaculty(@PathVariable int projectId, @PathVariable float ratings) {
        return studentService.updateScoreByFaculty(projectId, ratings);
    }
    @GetMapping("{studentId}/completed-projects")
    public ResponseEntity<List<Integer>> getCompletedProjects(@PathVariable int studentId){
        return studentService.getCompletedProjects(studentId);
    }





}
    