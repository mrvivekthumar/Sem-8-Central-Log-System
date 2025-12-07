package com.example.studentservice.Feign;


import com.example.studentservice.Vo.Faculty;
import com.example.studentservice.Vo.Project;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "faculty-service", url = "${faculty-service.url:http://localhost:8080}")
public interface FacultyInterface {
    @GetMapping("/api/project/visible")
    public ResponseEntity<List<Project>> getVisibleProjects();

    @GetMapping("/api/project/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable int projectId);

    @GetMapping("/api/project/projects")
    public ResponseEntity<List<Project>> getAllProjects();

    @PostMapping(value = "api/reports/{projectId}/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitReport(@PathVariable("projectId") int projectId,
            @RequestPart("file") MultipartFile file);

    @GetMapping("/api/faculty/{facultyId}")
    ResponseEntity<Faculty> getFacultyById(@PathVariable int facultyId);

    @PostMapping("/api/faculty/projectsbyIds")
    ResponseEntity<List<Project>> getProjectsByIds(@RequestBody List<Integer> projectIds);

    @GetMapping("/api/project/{projectId}/is-complete")
    ResponseEntity<Boolean> isProjectComplete(@PathVariable int projectId);
}
