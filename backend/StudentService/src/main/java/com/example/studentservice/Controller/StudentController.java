package com.example.studentservice.Controller;

import com.example.studentservice.Vo.Project;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("student")
public class StudentController {
    @Autowired
    private StudentService studentService;

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


}
    