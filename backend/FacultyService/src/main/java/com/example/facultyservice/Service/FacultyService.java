package com.example.facultyservice.Service;

import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
public class FacultyService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FacultyDao facultyDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectService projectService;

    private final String STUDENT_SERVICE = "http://localhost:8765/STUDENT-SERVICE/api/studentProject/";
    private final String STUDENT="http://localhost:8765/STUDENT-SERVICE/student";


    public ResponseEntity<Faculty> registerFaculty(Faculty faculty) {
        try {
            return new ResponseEntity<>(facultyDao.save(faculty), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<List<Project>> getProjectsById(int facultyId) {
        try {
            Faculty faculty = facultyDao.findById(facultyId).get();
            List<Project> projects = projectDao.findByFacultyId(faculty.getF_id());
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<List<String>> getProjectsByName(int fId) {
        try {
            Faculty faculty = facultyDao.findById(fId).get();
            List<Project> projects = projectDao.findByFacultyId(faculty.getF_id());
            List<String> projectNames = new ArrayList<>();
            for (Project project : projects) {
                projectNames.add(project.getTitle());
            }
            return new ResponseEntity<>(projectNames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteProject(int p_id) {
        try {
            projectDao.deleteById(p_id);
            return new ResponseEntity<>("Project deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Student>> getStudentsByProject(int projectId) {
        try {
            ResponseEntity<List<Student>> responseEntity = restTemplate.exchange(
                    STUDENT_SERVICE + "/" + projectId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Student>>() {
                    }
            );
            List<Student> students = responseEntity.getBody();
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }

    public ResponseEntity<Integer> getStudentCountByProjectId(int projectId) {
        try {
            ResponseEntity<Integer> responseEntity = restTemplate.exchange(STUDENT_SERVICE + "/" + projectId + "/student-count", HttpMethod.GET, null, Integer.class);
            Integer count = responseEntity.getBody();
            System.out.println(count.intValue());
            if (count != null) {
                return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<String> getApprovedStudent(int facultyId,int projectId,int studentId) {
        try{
            Project project=projectDao.findById(projectId).get();
            String updateUrl=STUDENT_SERVICE+"/updateStatus/"+studentId+"/"+projectId;
            restTemplate.put(updateUrl,null);
            if(project==null){
                return new ResponseEntity<>("Project Not found",HttpStatus.NOT_FOUND);
            }
            if(project.getFaculty().getF_id()!=facultyId){

                return new ResponseEntity<>("Unauthorized faculty for this project", HttpStatus.UNAUTHORIZED);
            }
            ResponseEntity<Student> responseEntityStudent=restTemplate.exchange("http://localhost:8765/STUDENT-SERVICE/student/"+studentId,
                    HttpMethod.GET,
                    null,
                    Student.class);
            Student student=null;
            if(responseEntityStudent.getStatusCode()==HttpStatus.OK){
                student=responseEntityStudent.getBody();
            }
            if(student.getStudentAvaibility()!= StudentAvaibility.AVAILABLE){
                return new ResponseEntity<>("Student is aleready asigned ",HttpStatus.CONFLICT);
            }
            project.setStatus(Status.APPROVED);
            projectDao.save(project);
            student.setStudentAvaibility(StudentAvaibility.NOT_AVAILABLE);
            restTemplate.put(STUDENT+"/"+studentId,student);



            return new ResponseEntity<>("Project is given to student "+student.getName()+ " by "+project.getFaculty().getName(),HttpStatus.OK);

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

    public ResponseEntity<List<Project>> createProjects(List<Project> projects,int facultyId) {
        try{
            for(Project p:projects){
                p.setFaculty(facultyDao.findById(facultyId).get());
            }
            return new ResponseEntity<>(projectDao.saveAll(projects),HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}