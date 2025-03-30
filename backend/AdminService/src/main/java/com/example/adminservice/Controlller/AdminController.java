package com.example.adminservice.Controlller;

import com.example.adminservice.Model.Faculty;
import com.example.adminservice.Feign.FacultyClient;
import com.example.adminservice.Model.Student;
import com.example.adminservice.Service.AdminService;
import com.example.adminservice.Vo.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
    @PostMapping("faculty/register")
    public ResponseEntity<String> registerFaculty(@RequestBody Faculty faculty) {
        System.out.println("In Controller");
        return adminService.registerFaculty(faculty);
    }
    @PostMapping("student/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        System.out.println("Admin student controller");
        return adminService.registerStudent(student);
    }
    @PostMapping("student/registerFile")
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file){
        System.out.println("Hey Hey");
        return adminService.registerFile(file);
    }
    @PostMapping("faculty/registerFile")
    public ResponseEntity<String> registerFaculties(@RequestPart("file") MultipartFile file){
        System.out.println("Hey Hey");
        return adminService.registerFileForFaculty(file);
    }
    @GetMapping("faculty/count")
    public ResponseEntity<Integer> getFacultyCount(){
        return  adminService.getFacultyCount();
    }
    @GetMapping("student/count")
    public ResponseEntity<Integer> getStudentCount(){
        return  adminService.getStudentCount();
    }
    @PostMapping("updatePassword")
    public ResponseEntity<String> updatePass(UserCredential user){
        return adminService.updatePassword(user);

    }


}
