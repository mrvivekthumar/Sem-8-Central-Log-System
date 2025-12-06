package com.example.adminservice.Controlller;

import com.example.adminservice.Model.Faculty;
import com.example.adminservice.Feign.FacultyClient;
import com.example.adminservice.Model.Student;
import com.example.adminservice.Service.AdminService;
import com.example.adminservice.Vo.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @GetMapping("hello")
    public String hello() {
        logger.debug("Received request to /hello");
        return "hello";
    }

    @PostMapping("faculty/register")
    public ResponseEntity<String> registerFaculty(@RequestBody Faculty faculty) {
        logger.info("Received faculty registration request for: {}", faculty.getEmail());
        return adminService.registerFaculty(faculty);
    }

    @PostMapping("student/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        logger.info("Received student registration request for: {}", student.getEmail());
        return adminService.registerStudent(student);
    }

    @PostMapping("student/registerFile")
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file) {
        logger.info("Received bulk student registration file: {}", file.getOriginalFilename());
        return adminService.registerFile(file);
    }

    @PostMapping("faculty/registerFile")
    public ResponseEntity<String> registerFaculties(@RequestPart("file") MultipartFile file) {
        logger.info("Received bulk faculty registration file: {}", file.getOriginalFilename());
        return adminService.registerFileForFaculty(file);
    }

    @GetMapping("faculty/count")
    public ResponseEntity<Integer> getFacultyCount() {
        return adminService.getFacultyCount();
    }

    @GetMapping("student/count")
    public ResponseEntity<Integer> getStudentCount() {
        return adminService.getStudentCount();
    }

    @PostMapping("updatePassword")
    public ResponseEntity<String> updatePass(UserCredential user) {
        return adminService.updatePassword(user);
    }
}
