package com.example.facultyservice.Controller;

import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Dto.NotificationRequest;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Student;
import com.example.facultyservice.Service.FacultyService;
import com.example.facultyservice.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FacultyService facultyService;

    @PostMapping("{facultyId}")
    public ResponseEntity<Project> createProject(@PathVariable int facultyId, @RequestBody Project project) {
//        System.out.println("value is "+facultyId); // this is just for debugging
        return projectService.createProject(project, facultyId);
    }
    @GetMapping("{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable int projectId){
        return projectService.getProjectById(projectId);
    }
    @PutMapping("status/{p_id}")
    public ResponseEntity<Project>updateStatus(@PathVariable int p_id){
        return projectService.updateStatus(p_id);

    }
    @GetMapping("projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return projectService.getAllProjeects();
    }

    @GetMapping("{projectId}/student-count")
    public ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId) {
        return facultyService.getStudentCountByProjectId(projectId);

    }
    @GetMapping("/visible")
    public ResponseEntity<List<Project>> getVisibleProjects(){
        return projectService.getVisibleProjects();
    }
    @GetMapping("/rejected")
    public ResponseEntity<List<Project>> getRejectedProjects(){
        return projectService.updateExpiredProjects();
    }

    @PutMapping("{projectId}/completed")
    public ResponseEntity<Project> completeProject(@PathVariable int projectId){
        System.out.println(projectId +" in faculty controller");
        return projectService.completeProject(projectId);
    }
    @PutMapping("{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable int projectId,@RequestBody Project updatedProject){
        return projectService.updateProject(projectId,updatedProject);
    }
    @GetMapping("byFaculty")
    public ResponseEntity<List<Project>>getFacultyProjects(
            @RequestParam int facultyId,
            @RequestParam List<Integer> projectIds
    ){
        return projectService.getFacultyProjects(facultyId,projectIds);


    }


}
