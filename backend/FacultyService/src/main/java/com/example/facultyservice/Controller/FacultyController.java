package com.example.facultyservice.Controller;

import com.example.facultyservice.Model.Faculty;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Student;
import com.example.facultyservice.Service.FacultyService;
import com.example.facultyservice.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


@RestController
@RequestMapping("/api/faculty")
public class FacultyController {


    @Autowired
    private FacultyService facultyService;
    @Autowired
    private ProjectService projectService;

    @PostMapping("/register")
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
    @PostMapping("{facultyId}/studentproject/{projectId}/approved")
    public ResponseEntity<String> getApprovedStudent(@PathVariable int facultyId,@PathVariable int projectId,@RequestBody List<Integer>studentIds){
        return facultyService.getApprovedStudent(facultyId,projectId,studentIds);
    }
    @PostMapping("{facultyId}/projects")
    public ResponseEntity<List<Project>> createProjects(@RequestBody List<Project> projects,@PathVariable int facultyId){
        return facultyService.createProjects(projects,facultyId);
    }
    @PostMapping(value = "/registerFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file){
        return facultyService.registerFileForFaculty(file);

    }
    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(){
        return facultyService.getCount();

    }
    @GetMapping("/email/{email}")
    public ResponseEntity<Faculty> getFacultyByEmail(@PathVariable String email){
        return facultyService.findByEmail(email);
    }
    @PostMapping("/projectsbyIds")
    public ResponseEntity<List<Project>> getProjectsByIds(@RequestBody List<Integer> projectIds){
        return projectService.getProjectsByIds(projectIds);

    }

    @GetMapping("all")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return facultyService.findAll();
    }
    @GetMapping("/approvedProject")
    public ResponseEntity<Integer> getTotalApprovedProjects(){
        return facultyService.getTotalApprovedProjects();
    }
    @PutMapping("/project/{projectId}/ratings/{ratings}")
    public ResponseEntity<String> updateRatings(@PathVariable int projectId, @PathVariable float ratings){
        System.out.println("Hy Are you printing");
        return facultyService.updateRatings(projectId,ratings);
    }
    @GetMapping("project/{projectId}/is-complete")
    public ResponseEntity<Boolean> getIsComplete(@PathVariable int projectId){
        return facultyService.getIsComplete(projectId);
    }
}
