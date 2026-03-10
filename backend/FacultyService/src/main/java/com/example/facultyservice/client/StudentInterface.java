package com.example.facultyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.facultyservice.entity.Student;

import java.util.List;
import java.util.Map;

/**
 * Feign client for Student Service
 * Student Service has context-path: /student
 * StudentController has @RequestMapping("/students")
 * So full path: /student/students/...
 */
@FeignClient(name = "student-service", url = "${services.student.url:http://student-service:8083}")
public interface StudentInterface {

    @PutMapping("/student/students/available/{projectId}")
    ResponseEntity<String> updateStudentsAvailable(@PathVariable int projectId);

    @GetMapping("/student/studentProject/students/{projectId}")
    ResponseEntity<List<Map<String, Object>>> getApplicationsByProject(@PathVariable int projectId);

    @GetMapping("/student/studentProject/{projectId}/student-count")
    ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId);

    @PostMapping("/student/students/byIds")
    ResponseEntity<List<Student>> getStudentsById(@RequestBody List<Integer> studentIds);

    @PutMapping("/student/students/{studentId}/unavailable")
    void makeUnavailable(@PathVariable int studentId);

    @PutMapping("/student/studentProject/updateStatus/{studentId}/{projectId}")
    ResponseEntity<Object> updateApplicationStatus(@PathVariable int studentId, @PathVariable int projectId,
            @RequestBody Map<String, String> body);

    @PutMapping("/student/studentProject/{projectId}/rating/{rating}")
    ResponseEntity<String> updateRatings(@PathVariable int projectId, @PathVariable float rating);

    @GetMapping("/student/students/allIds")
    ResponseEntity<List<Integer>> getAllStudentIds();
}
