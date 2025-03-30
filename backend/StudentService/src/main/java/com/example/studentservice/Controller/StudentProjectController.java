package com.example.studentservice.Controller;

import com.example.studentservice.Model.Student;
import com.example.studentservice.Model.StudentProject;
import com.example.studentservice.Service.StudentProjectService;
import com.example.studentservice.Vo.Project;
import feign.Param;
import feign.Response;
import jakarta.ws.rs.Path;
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
    @GetMapping("approvedProject/{studentId}")
    public ResponseEntity<Integer> getApprovedProject(@PathVariable int studentId){
        return studentProjectService.getApprovedProject(studentId);

    }
    @GetMapping("appliedProject/{studentId}")
    public ResponseEntity<List<Integer>> getAppliedProjectsIds(@PathVariable int studentId){
        System.out.println("Are you there");;
        return studentProjectService.getApliedProjectsIds(studentId);
    }
    @PatchMapping("updatePreference/{studentId}/project/{projectId}/{newPreference}")
    public ResponseEntity<String> updateProjectPreference(@PathVariable int studentId,@PathVariable int projectId,@PathVariable int newPreference) {
        return studentProjectService.updateProjectPrefernce(studentId,projectId,newPreference);
    }
    @GetMapping("projectIdsByPref/{studentId}")
    public ResponseEntity<List<Integer>>getProjectIdsByPref(@PathVariable int studentId){
        return studentProjectService.getProjIdsByPref(studentId);
    }
    @GetMapping("{projectId}/student/{studentId}")
    public ResponseEntity<StudentProject>getStudentProject(@PathVariable int projectId,@PathVariable int studentId){
        return studentProjectService.findProjectByProjIdAndStudId(projectId,studentId);
    }
    @PostMapping("{projectId}/student")
    public ResponseEntity<List<StudentProject>>getStudentProjectByIds(@PathVariable int projectId,@RequestBody List<Integer> studentIds){
        return studentProjectService.findProjectByProjIdAndStudIds(projectId,studentIds);
    }
    @GetMapping("{projectId}/aprovedStudents")
    public ResponseEntity<List<Integer>> getApprovedStudentsByProjectId(@PathVariable int projectId){
        return studentProjectService.getApprovedStudentsByProjectId(projectId);

    }
    @GetMapping("{studentId}/pendingApprovals")
    public ResponseEntity<List<Integer>> getStudentPendingApprovals(@PathVariable int studentId){
        return studentProjectService.getStudentPendingApprovals(studentId);
    }
    @GetMapping("{studentId}/getProjectFaculties/project/{projectId}")
    public ResponseEntity<List<Integer>> geIdsOfFacultiesByProject(@PathVariable int studentId,@PathVariable int projectId){
        return studentProjectService.getFacultiesByProject(studentId,projectId);
    }
    @GetMapping("{projectId}/completeIds")
    public ResponseEntity<List<Integer>> getCompletedStudentIds(@PathVariable int projectId){
        return studentProjectService.findCompletedStudentIds(projectId);
    }


}
