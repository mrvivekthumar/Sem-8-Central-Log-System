package com.example.studentservice.controller;

import com.example.studentservice.domain.Student;
import com.example.studentservice.security.CurrentUser;
import com.example.studentservice.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/me")
public class StudentMeController {

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private StudentService studentService;

    /*
     * =========================
     * GET MY PROFILE
     * =========================
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudent(@PathVariable int studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    /*
     * =========================
     * UPDATE MY PROFILE
     * =========================
     */
    @PutMapping
    public ResponseEntity<Student> updateMyProfile(@RequestBody Student student) {
        int studentId = currentUser.getUserId().intValue();
        return ResponseEntity.ok(studentService.updateStudentDetails(studentId, student));
    }
}
