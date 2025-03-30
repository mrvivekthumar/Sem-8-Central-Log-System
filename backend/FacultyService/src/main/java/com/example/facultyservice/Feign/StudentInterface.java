package com.example.facultyservice.Feign;

import com.example.facultyservice.Model.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentInterface {

    @PutMapping("students/available/{projectId}")
    ResponseEntity<String> updateStudentsAvailable(@PathVariable int projectId);
    @PutMapping("students/projects/{projectId}/ratings/{ratings}")
    ResponseEntity<String> updateRatings(@PathVariable int projectId, @PathVariable float ratings);
}
