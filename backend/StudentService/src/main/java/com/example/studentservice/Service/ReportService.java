package com.example.studentservice.Service;

import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Feign.FacultyInterface;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Model.StudentProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportService {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentProjectDao studentProjectDao;
    @Autowired
    private FacultyInterface facultyInterface;


    public ResponseEntity<String> submitReport(int studentId, int projectId, MultipartFile file) {
        try{
            ResponseEntity<Student> student=studentService.findStudentById(studentId);
//            StudentProject studentProject = studentProjectDao.findByStudentAndProjectId(student, projectId)
//                    .orElseThrow(() -> new Exception("Student not assigned to this project"));
            System.out.println("Before Interface");

            facultyInterface.submitReport(projectId,file);
            System.out.println("After Interface");
            return new ResponseEntity<>("Project is submited",HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Fuked Up"+"  "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
