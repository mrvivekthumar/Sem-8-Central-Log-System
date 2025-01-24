package com.example.studentservice.Controller;

import com.example.studentservice.Dao.StudentDao;
import com.example.studentservice.Model.PersonalProject;
import com.example.studentservice.Service.StudentProjectService;
import com.example.studentservice.Vo.Project;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Service.StudentService;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
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
        return studentService.registerStudent(student);
    }
    @PostMapping("registerFile")
    public ResponseEntity<String> registerFile(@RequestParam("file") MultipartFile file){
        return studentService.registerFile(file);
    }
    @PostMapping("apply/{studentId}/project/{projectId}")
    public ResponseEntity<String> applyProject(@PathVariable int studentId,@PathVariable int projectId){
        return studentService.applyProject(studentId,projectId);

    }
    @GetMapping("projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return studentService.getAllProjets();
    }
    @PutMapping("{studentId}")
    public ResponseEntity<String> makeUnavailable(@RequestBody Student student){
        return studentService.makeUnavailibity(student);
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




}
    