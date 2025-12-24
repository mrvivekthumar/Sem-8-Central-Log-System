package com.example.facultyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.facultyservice.domain.Student;

import java.util.List;

@FeignClient(name = "student-service", url = "${student-service.url:http://localhost:8084}")
public interface StudentInterface {

    @PutMapping("students/available/{projectId}")
    ResponseEntity<String> updateStudentsAvailable(@PathVariable int projectId);

    @GetMapping("/api/studentProject/students/{projectId}")
    ResponseEntity<List<Student>> getStudents(@PathVariable int projectId);

    @GetMapping("/api/studentProject/{projectId}/student-count")
    ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId);

    @PostMapping("/students/byIds")
    ResponseEntity<List<Student>> getStudentsById(@RequestBody List<Integer> studentIds);

    @PutMapping("/students/{studentId}/unavailable")
    void makeUnavailable(@PathVariable int studentId);

    @PutMapping("/api/studentProject/updateStatus/{studentId}/{projectId}")
    void updateStatus(@PathVariable int studentId, @PathVariable int projectId);

    @PutMapping("/api/studentProject/{projectId}/rating/{rating}")
    ResponseEntity<String> updateRatings(@PathVariable int projectId, @PathVariable float rating);
}
