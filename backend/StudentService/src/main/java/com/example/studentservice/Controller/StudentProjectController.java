package com.example.studentservice.Controller;

import com.example.studentservice.Model.Student;
import com.example.studentservice.Model.StudentProject;
import com.example.studentservice.Service.StudentProjectService;
import com.example.studentservice.Vo.Project;
import feign.Param;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/studentProject")
public class StudentProjectController {

    @Autowired
    private StudentProjectService studentProjectService;
    @GetMapping("{projectId}")
    public ResponseEntity<List<Student>> getStudents(@PathVariable int projectId){
        return studentProjectService.getStudentIds(projectId);

    }
    @GetMapping("{projectId}/student-count")
    public ResponseEntity<Integer> getStudentCountByProject(@PathVariable int projectId) {
        return studentProjectService.getStudentCountByProjectId(projectId);
    }
    @GetMapping("student/{projectId}")
    public ResponseEntity<List<StudentProject>> getAllStud(@PathVariable int projectId){
        return studentProjectService.getAllStudentsBYProj(projectId);
    }
    @PutMapping("updateStatus/{studentId}/{projectId}")
    public ResponseEntity<String> updateStatus(@PathVariable int studentId, @PathVariable int projectId){

        return studentProjectService.updateStatus(studentId,projectId);

    }
    @GetMapping("{studentId}/project/{projectId}/status")
    public ResponseEntity<Boolean>checkApplicationStatus(@PathVariable int studentId,@PathVariable int projectId){
        return studentProjectService.checkApplicationStatus(studentId,projectId);

    }
    @GetMapping("/appliedProjects")
    public ResponseEntity<List<Integer>> getAppliedProjectIds(){
        return studentProjectService.getAppliedProjectIds();
    }

}
