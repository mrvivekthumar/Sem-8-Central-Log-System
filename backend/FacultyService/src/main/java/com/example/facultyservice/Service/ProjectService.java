package com.example.facultyservice.Service;

import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Model.Faculty;
import com.example.facultyservice.Model.Project;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
            Faculty faculty = facultyDao.findById(facultyId).get();
            System.out.println("Faculty: " + faculty);
            if(faculty == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            project.setFaculty(faculty);

            ResponseEntity<Project> response = new ResponseEntity<>(projectDao.save(project), HttpStatus.OK);
            rabbitTemplate.convertAndSend(exchange, routingKey, project);
            System.out.println("Project sent to RabbitMQ: " + project);
            System.out.println("Hii Bro");
            System.out.println(project.getDescription());
            if (response.getStatusCode().is2xxSuccessful()) {
                messagingTemplate.convertAndSend("/topic/notify-students",
                        "New project created: " + project.getTitle());
                System.out.println("Project created: " + project.getTitle());
            }
            return response;
        } catch (Exception e) {
            System.out.println("Error in ProjectService: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public ResponseEntity<List<Project>> getAllProjectsByFacultyId(int fId) {
//
//    }
}
