package com.example.studentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.studentservice.client.dto.Faculty;
import com.example.studentservice.client.dto.Project;
import java.util.List;

/**
 * Feign client for Faculty Service
 * Faculty Service has context-path: /faculty
 * FacultyController has @RequestMapping("")
 * ProjectController has @RequestMapping("/projects")
 */
@FeignClient(name = "faculty-service", url = "${services.faculty.url:http://faculty-service:8082}")
public interface FacultyInterface {

    @GetMapping("/faculty/projects/visible")
    ResponseEntity<List<Project>> getVisibleProjects();

    @GetMapping("/faculty/projects/{projectId}")
    ResponseEntity<Project> getProjectById(@PathVariable int projectId);

    @GetMapping("/faculty/projects")
    ResponseEntity<List<Project>> getAllProjects();

    @PostMapping(value = "/faculty/reports/{projectId}/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> submitReport(@PathVariable("projectId") int projectId,
            @RequestPart("file") MultipartFile file);

    @GetMapping("/faculty/{facultyId}")
    ResponseEntity<Faculty> getFacultyById(@PathVariable int facultyId);

    @PostMapping("/faculty/projects/bulk")
    ResponseEntity<List<Project>> getProjectsByIds(@RequestBody List<Integer> projectIds);

    @GetMapping("/faculty/projects/{projectId}/is-complete")
    ResponseEntity<Boolean> isProjectComplete(@PathVariable int projectId);

    @PostMapping("/faculty/notifications/send")
    ResponseEntity<Object> sendNotification(@RequestBody java.util.Map<String, Object> notificationRequest);
}
