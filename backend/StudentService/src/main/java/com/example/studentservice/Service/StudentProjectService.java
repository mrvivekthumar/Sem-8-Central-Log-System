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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    studProj.stream().forEach(s -> {
        if (s.getStudent().getStudentId() == studentId) {
            s.setStatus(Status.APPROVED); // Approve the given student
        } else if (s.getStatus() != Status.APPROVED) {
            // Only reject those who are not already approved
            s.setStatus(Status.REJECTED);
        }
    });



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

    public ResponseEntity<Integer> getApprovedProject(int studentId) {
        try{
            List<StudentProject> projects=studentProjectDao.findByStudent_StudentId(studentId);
            for(StudentProject p:projects){
                if(p.getStatus()==Status.APPROVED){
                    return new ResponseEntity<>(p.getProjectId(),HttpStatus.OK);
                }
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return null;
    }

    public ResponseEntity<List<Integer>> getApliedProjectsIds(int studentId) {
        try{
            System.out.println("Yes I am here bro ");
            List<Integer> projectIds=studentProjectDao.findProjectIdsByStudent_StudentId(studentId);
            return new ResponseEntity<>(projectIds,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updateProjectPrefernce(int studentId, int projectId, int newPreference) {
        try{
            List<StudentProject> appliedProjects=studentProjectDao.findProjectsByStudentOrdered(studentId);
            StudentProject targetProject=appliedProjects.stream().
                    filter(p->p.getProjectId()==projectId).
                    findFirst().
                    orElse(null);
            if(targetProject==null){
                return new ResponseEntity<>("Project not found for this tudent",HttpStatus.NOT_FOUND);

            }
            int oldPreference=targetProject.getPreference();
            if(oldPreference==newPreference){
                return new ResponseEntity<>("Preference is aleready set",HttpStatus.OK);
            }
            if(newPreference<oldPreference){
                appliedProjects.forEach(p->{
                    if(p.getPreference() >= newPreference && p.getPreference()<oldPreference){
                        p.setPreference(p.getPreference()+1);
                    }
                });
            }
            else{
                appliedProjects.forEach(p->{
                    if(p.getPreference() <=newPreference && p.getPreference()>oldPreference){
                        p.setPreference(p.getPreference()-1);
                    }
                });
            }
            targetProject.setPreference(newPreference);
            studentProjectDao.saveAll(appliedProjects);
            return new ResponseEntity<>("Project prefrence updated",HttpStatus.OK);

        }
        catch(Exception e){
            return  new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> getProjIdsByPref(int studentId) {
        try{
            List<Integer>projectIds=studentProjectDao.findProjectIdsByStudentOrdered(studentId);
            if(projectIds==null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(projectIds,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<StudentProject> findProjectByProjIdAndStudId(int projectId, int studentId) {
        try{
            StudentProject studentProject=studentProjectDao.findByStudent_StudentIdAndProjectId(studentId,projectId);
            return new ResponseEntity<>(studentProject,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<StudentProject>> findProjectByProjIdAndStudIds(int projectId, List<Integer> studentIds) {
        try{
           List<StudentProject> studentProjects=studentProjectDao.findByProjectIdAndStudent_StudentIdIn(projectId,studentIds);
           return new ResponseEntity<>(studentProjects,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> getApprovedStudentsByProjectId(int projectId) {
        try{
            List<Integer> studentIds=studentProjectDao.findStudentIdByApproved(projectId);
            return new ResponseEntity<>(studentIds,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> getStudentPendingApprovals(int studentId) {
        try{
            Optional<List<Integer>> studentProjects=studentProjectDao.findProjectIdByStudent_StudentIdAndStatus(studentId,Status.PENDING);
            if(studentProjects.isPresent()){
                return new ResponseEntity<>(studentProjects.get(),HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<List<Integer>> getFacultiesByProject(int studentId,int projectId) {
        try{
            Optional<List<Integer>> studentProjects=studentProjectDao.findProjectIdByStudent_StudentIdAndStatus(studentId,Status.PENDING);
            if(studentProjects.isPresent()){
               List<Integer> projectIds=studentProjects.get().
                       stream().
                       filter(p->!p.equals(projectId)).
                       collect(Collectors.toList());
                System.out.println(projectIds);

               return new ResponseEntity<>(projectIds,HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<List<Student>> getTeamMates(int projectId) {
        try{
            List<Student> students=new ArrayList<>();
            List<Integer> studentIds=studentProjectDao.findStudent_StudentIdByProjectId(projectId);
            for(Integer studentId:studentIds){
                Student student=studentDao.findStudentByStudentId(studentId);
                students.add(student);
            }
            return new ResponseEntity<>(students,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> findStudentIdsByProjectIdAndStatus(int projectId, Status status) {
        try{
            List<Integer>ids=studentProjectDao.findStudentIdByProjectIdAndStatus(projectId,status);
            System.out.println("Students Ids are " + ids);
            return new ResponseEntity<>(ids,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> findCompletedStudentIds(int projectId) {
        try {
            List<Integer>ids=studentProjectDao.findIdsCompletedStudentsByProjectId(projectId, Arrays.asList(Status.COMPLETED,Status.APPROVED));
            return new ResponseEntity<>(ids,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Integer> findCompletedProjects(int studentId) {
        try{
            List<StudentProject> projects=studentProjectDao.findByStudent_StudentId(studentId);
            List<Integer> projectIds=new ArrayList<>();
            for(StudentProject p:projects){
                if(p.getStatus()==Status.COMPLETED){
                    projectIds.add(p.getProjectId());
                }
            }
            return projectIds;
        } catch (Exception e) {
            return null;
        }
    }
}
