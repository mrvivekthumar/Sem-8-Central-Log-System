package com.example.facultyservice.Service;

import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.notification.model.NotificationRequest;
import com.example.facultyservice.notification.model.NotificationType;
import com.example.facultyservice.notification.model.ReceiverType;
import com.example.facultyservice.notification.model.SenderType;
import com.example.facultyservice.Feign.NotificationInterface;
import com.example.facultyservice.Feign.StudentInterface;
import com.example.facultyservice.Model.Faculty;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Status;
import feign.Param;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Feign.NotificationInterface;
import com.example.facultyservice.Feign.StudentInterface;
import com.example.facultyservice.Model.Faculty;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Status;
import feign.Param;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Autowired
    private StudentInterface studentInterface;
    @Value("${rabbitmq.routing-key}")
    private String routingKey;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private FacultyDao facultyDao;
    @Autowired
    private NotificationInterface notificationInterface;

    public ResponseEntity<Project> createProject(Project project, int facultyId) {
        try {
            Optional<Faculty> optionalFaculty = facultyDao.findById(facultyId);
            if (optionalFaculty.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Faculty not found
            }
            Faculty faculty = optionalFaculty.get();

            // Associate the faculty with the project
           project.setFaculty(faculty);

            // Save the project
            Project savedProject = projectDao.save(project);
            System.out.println(project);
            ResponseEntity<Project> response = new ResponseEntity<>(projectDao.save(project), HttpStatus.OK);

//            NotificationRequest notification=new NotificationRequest();
//            notification.setSenderType(SenderType.FACULTY);
//            notification.setSenderId(String.valueOf(facultyId));
//            notification.setReceiverType(ReceiverType.STUDENT);
//            notification.setReceiverId("ALL"); // Notify all students
//            notification.setNotificationType(NotificationType.PROJECT_CREATION);
//            notification.setTitle("New Project Available");
//            notification.setMessage("A new project '" + project.getTitle() + "' has been posted!");
////            notification.setSeen(false);
//
//            notificationInterface.sendNotification(notification); // 🚀 Feign call to Notification Service
            return response;
        } catch (Exception e) {
            System.out.println("Error in ProjectService: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Project> getProjectById(int projectId) {
        Optional<Project> optionalProject = projectDao.findById(projectId);
        if (optionalProject.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalProject.get(), HttpStatus.OK);
    }


    public ResponseEntity<Project> updateStatus(int projectId) {
        Optional<Project> project=projectDao.findById(projectId);
       Project projectupdate=project.get();
       projectupdate.setStatus(Status.PENDING);
       projectDao.save(projectupdate);
        return new ResponseEntity<>(projectupdate,HttpStatus.OK);
    }

    public ResponseEntity<List<Project>> getAllProjeects() {
        try{
            return new ResponseEntity<>(projectDao.findAll(),HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Project>> getVisibleProjects() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            System.out.println(currentTime);

            List<Project> visibleProjects = projectDao.findVisibleProjects(currentTime, Status.OPEN_FOR_APPLICATIONS);

            // Return empty list instead of null or 204
            if (visibleProjects == null || visibleProjects.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(visibleProjects);

        } catch (Exception e) {
            System.out.println("Exception in getVisibleProjects: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<Project>> updateExpiredProjects(){
        try{
            LocalDateTime currentTime=LocalDateTime.now();
            List<Project> expiredProjects=projectDao.findExpiredProjects(Status.OPEN_FOR_APPLICATIONS,currentTime.minusHours(24));
            return new ResponseEntity<>(expiredProjects,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    public ResponseEntity<Project> completeProject(int projectId) {
        try{
            System.out.println(projectId +" in faculty service");
            Project project=projectDao.findById(projectId).get();
            if(project==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            project.setStatus(Status.COMPLETED);
            System.out.println("Hey Before client");
            ResponseEntity<String>st=studentInterface.updateStudentsAvailable(projectId);
            System.out.println(st);
            System.out.println("Hey AAfter client");
            projectDao.save(project);
            return new ResponseEntity<>(project,HttpStatus.OK);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Project> updateProject(int projectId, Project updatedProject) {
        try{
            Optional<Project> p=projectDao.findById(projectId);
            Project project=null;
            if(p.isPresent()){
                project=p.get();
                project.setTitle(updatedProject.getTitle());
                project.setDescription(updatedProject.getDescription());
                project.setDeadline(updatedProject.getDeadline());
                project.setApplicationDeadline(updatedProject.getApplicationDeadline());
                project.setSkills(updatedProject.getSkills());
                projectDao.save(project);

            }
            return new ResponseEntity<>(project,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseEntity<List<Project>> getFacultyProjects(int facultyId, List<Integer> projectIds) {
        try{

            List<Project> projects=projectDao.findByFacultyIdAndProjectIds(facultyId,projectIds);
            return new ResponseEntity<>(projects,HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<List<Project>> getProjectsByIds(List<Integer> projectIds) {
        try {
            List<Project>projects=projectDao.findByProjectIds(projectIds);
            return new ResponseEntity<>(projects,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

