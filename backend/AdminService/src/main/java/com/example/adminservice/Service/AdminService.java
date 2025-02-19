package com.example.adminservice.Service;

import com.example.adminservice.Feign.AuthInterface;
import com.example.adminservice.Feign.FacultyClient;
import com.example.adminservice.Feign.StudentClient;
import com.example.adminservice.Model.Faculty;
import com.example.adminservice.Model.Student;
import com.example.adminservice.Vo.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminService {
    @Autowired
    private StudentClient studentClient;
    @Autowired
    private FacultyClient facultyClient;
    @Autowired
    private AuthInterface authInterface;

    public ResponseEntity<Faculty> registerFaculty(Faculty faculty) {
        try{
            return new ResponseEntity<>(facultyClient.registerFaculty(faculty), HttpStatus.OK).getBody();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Student> registerStudent(Student student) {
        try{
            return new ResponseEntity<>(studentClient.registerStudent(student), HttpStatus.OK).getBody();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> registerFile(MultipartFile file) {
        try{
            System.out.println("Hey Hey Service");
            studentClient.registerFile(file);
            System.out.println("Fuck you");
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> registerFileForFaculty(MultipartFile file) {
        try{
            System.out.println("Hey Hey Faculties");
            facultyClient.registerFile(file);
            System.out.println("Fuck you");
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> getFacultyCount() {
        try{
            Integer count=facultyClient.getCount();
            return new ResponseEntity<>(count,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Integer> getStudentCount() {
        try{
            Integer count=studentClient.getCount();
            return new ResponseEntity<>(count,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updatePassword(UserCredential user) {
        try{
            ResponseEntity<String> status=authInterface.updatePassword(user);
            return status;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
