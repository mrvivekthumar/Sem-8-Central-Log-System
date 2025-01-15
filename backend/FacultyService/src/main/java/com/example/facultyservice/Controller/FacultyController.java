package com.example.facultyservice.Controller;

import com.example.facultyservice.Model.Faculty;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
