package com.example.facultyservice.Feign;

import com.example.facultyservice.Model.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentInterface {

    @PutMapping("students/available/{projectId}")
    ResponseEntity<String> updateStudentsAvailable(@PathVariable int projectId);
    @PutMapping("students/projects/{projectId}/ratings/{ratings}")
    ResponseEntity<String> updateRatings(@PathVariable int projectId, @PathVariable float ratings);
    @PutMapping("students/{studentId}")
    ResponseEntity<String> makeUnavailable(@PathVariable int studentId);
    @PutMapping("/api/studentProject/updateStatus/{studentId}/{projectId}")
    ResponseEntity<String> updateStatus(@PathVariable int studentId, @PathVariable int projectId);
    @GetMapping("/api/studentProject/{projectId}")
    ResponseEntity<List<Student>> getStudents(@PathVariable int projectId);
    @GetMapping("/api/studentProject/{projectId}/student-count")
    ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId);
    @GetMapping("/students/by-id")
    ResponseEntity<List<Student>> getStudentsById(@RequestParam("ids") List<Integer> ids);


}
