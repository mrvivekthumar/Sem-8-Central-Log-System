package com.example.studentservice.Service;

import com.example.studentservice.Dao.StudentDao;
import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Model.StudentProject;
import com.example.studentservice.Vo.Project;
import com.example.studentservice.Vo.Status;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentProjectService {

    @Autowired
    private StudentProjectDao studentProjectDao;
    @Autowired
    private StudentDao studentDao;

    private final String FACULTY_SERVICE="http://localhost:8765/FACULTY-SERVICE/api/project";
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<List<Student>> getStudentIds(int projectId) {
        try{
            List<Student> students=new ArrayList<>();
            List<Integer> studentIds=studentProjectDao.findStudent_StudentIdByProjectId(projectId);
            for(Integer  studentId:studentIds){
                Student s=studentDao.findStudentByStudentId(studentId);
                students.add(s);
            }
            return new ResponseEntity<>(students,HttpStatus.OK);

        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<Integer> getStudentCountByProjectId(int projectId) {
        try{
            return new ResponseEntity<>(studentProjectDao.countStudentsByProjectId(projectId),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<List<Project>> getVisibleProjects() {
        try{
            ResponseEntity<List<Project>> responseEntity=restTemplate.exchange(FACULTY_SERVICE+"/"+"visible",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Project>>() {
                    }
            );
            List<Project>projects=null;
            if(responseEntity.getStatusCode()==HttpStatus.OK){
                projects=responseEntity.getBody();
            }
            if(projects==null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(projects,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<StudentProject>> getAllStudentsBYProj(int projectId) {
        try{
            List<StudentProject> studProj=studentProjectDao.findByProjectId(projectId);
            return new ResponseEntity<>(studProj,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<String> updateStatus(int studentId,int projectId){
        List<StudentProject> studProj=studentProjectDao.findByProjectId(projectId);
        System.out.println(studProj);
        for (StudentProject s : studProj) {
            if (s.getStudent().getStudentId() == studentId) {
                s.setStatus(Status.APPROVED); // Approve the given student
            } else if (s.getStatus() != Status.APPROVED) {
                // Only reject those who are not already approved
                s.setStatus(Status.REJECTED);
            }
        }
        studentProjectDao.saveAll(studProj);
        return new ResponseEntity<>("Status Are updated",HttpStatus.OK);

    }

    public ResponseEntity<Boolean> checkApplicationStatus(int studentId, int projectId) {
        try{
            boolean hasApplied= studentProjectDao.existsByStudent_StudentIdAndProjectId(studentId,projectId);
            return new ResponseEntity<>(hasApplied,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseEntity<List<Integer>> getAppliedProjectIds() {
        try{
            List<Integer>ids=studentProjectDao.findDistinctByProjectId();
            return new ResponseEntity<>(ids,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
