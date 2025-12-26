package com.example.studentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.studentservice.client.dto.Faculty;
import com.example.studentservice.client.dto.Project;
import java.util.List;

// FIXED: Port 8080 → 8082
@FeignClient(name = "faculty-service", url = "${faculty-service.url:http://localhost:8082}")
public interface FacultyInterface {

    // FIXED: Removed /api prefix (already stripped by Gateway)
    @GetMapping("/project/visible")
    ResponseEntity<List<Project>> getVisibleProjects();

    @GetMapping("/project/{projectId}")
    ResponseEntity<Project> getProjectById(@PathVariable int projectId);

    @GetMapping("/project/projects")
    ResponseEntity<List<Project>> getAllProjects();

    @PostMapping(value = "/reports/{projectId}/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> submitReport(@PathVariable("projectId") int projectId,
            @RequestPart("file") MultipartFile file);

    @GetMapping("/faculty/{facultyId}")
    ResponseEntity<Faculty> getFacultyById(@PathVariable int facultyId);

    @PostMapping("/faculty/projectsbyIds")
    ResponseEntity<List<Project>> getProjectsByIds(@RequestBody List<Integer> projectIds);

    @GetMapping("/project/{projectId}/is-complete")
    ResponseEntity<Boolean> isProjectComplete(@PathVariable int projectId);
}
