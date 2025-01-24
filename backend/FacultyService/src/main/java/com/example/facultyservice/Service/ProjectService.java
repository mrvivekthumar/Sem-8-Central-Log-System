package com.example.facultyservice.Service;

import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Dao.ProjectDao;
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
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private FacultyDao facultyDao;

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

            System.out.println("Project sent to RabbitMQ: " + project);
            System.out.println("Hii Bro");
            System.out.println(project.getDescription());
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
        try{
            LocalDateTime currentTime=LocalDateTime.now();
            System.out.println(currentTime);
            List<Project> visibleProjects=projectDao.findVisibleProjects(currentTime.minusHours(24),Status.OPEN_FOR_APPLICATIONS);

            if(visibleProjects==null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(visibleProjects,HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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


}
