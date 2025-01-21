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
    @GetMapping("{p_id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int p_id){
        return projectService.getProjectById(p_id);
    }
    @PutMapping("status/{p_id}")
    public ResponseEntity<Project>updateStatus(@PathVariable int p_id){
        return projectService.updateStatus(p_id);

    }
    @GetMapping("projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return projectService.getAllProjeects();
    }
    @PostMapping("notify")
    public ResponseEntity<String> notifyFaculty(@RequestBody NotificationRequest notificationRequest) {

        System.out.println("Notification received for project ID: " + notificationRequest.getProjectId());
        System.out.println("Student Details: " + notificationRequest.getStudent());

        // Return response
        return new ResponseEntity<>("Faculty notified successfully.", HttpStatus.OK);
    }
    @GetMapping("{projectId}/student-count")
    public ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId) {
        return facultyService.getStudentCountByProjectId(projectId);

    }
    @GetMapping("/visible")
    public ResponseEntity<List<Project>> getVisibleProjects(){
        return projectService.getVisibleProjects();
    }



}
