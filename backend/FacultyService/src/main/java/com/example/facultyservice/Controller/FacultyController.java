package com.example.facultyservice.Controller;

import com.example.facultyservice.Model.Faculty;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Student;
import com.example.facultyservice.Service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("api/faculty")
public class FacultyController {


    @Autowired
    private FacultyService facultyService;

    @PostMapping("register")
    public ResponseEntity<Faculty> registerFaculty(@RequestBody Faculty faculty) {
        return facultyService.registerFaculty(faculty);
    }
    @GetMapping("{f_id}")
    ResponseEntity<List<Project>>getProjectsById(@PathVariable int f_id){
        return facultyService.getProjectsById(f_id);
    }
    @GetMapping("projectsName/{f_id}")
    ResponseEntity<List<String>>getProjectsByName(@PathVariable int f_id){
        return facultyService.getProjectsByName(f_id);
    }
    @DeleteMapping("/project/{p_id}")
    ResponseEntity<String>deleteProject(@PathVariable int p_id){
        return facultyService.deleteProject(p_id);
    }

    @GetMapping("studentproject/{projectId}")
    public ResponseEntity<List<Student>> getStudentsByProject(@PathVariable int projectId){
        return facultyService.getStudentsByProject(projectId);
    }
    @GetMapping("studentproject/{projectId}/count")
    public ResponseEntity<Integer>getStudentCountByProjectId(@PathVariable int projectId){
        return facultyService.getStudentCountByProjectId(projectId);
    }
    @PostMapping("{facultyId}/studentproject/{projectId}/approved/{studentId}")
    public ResponseEntity<String> getApprovedStudent(@PathVariable int facultyId,@PathVariable int projectId,@PathVariable int studentId){
        return facultyService.getApprovedStudent(facultyId,projectId,studentId);
    }
    @PostMapping("{facultyId}/projects")
    public ResponseEntity<List<Project>> createProjects(@RequestBody List<Project> projects,@PathVariable int facultyId){
        return facultyService.createProjects(projects,facultyId);
    }
    @PostMapping(value = "registerFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file){
        return facultyService.registerFileForFaculty(file);

    }
}
