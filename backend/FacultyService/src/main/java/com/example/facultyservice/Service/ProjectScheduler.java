package com.example.facultyservice.Service;

import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectScheduler {
    @Autowired
    private  ProjectService projectService;
    @Autowired
    private ProjectDao projectDao;

    //@Scheduled(cron = "0 * * * * ?")
    public void scheduleProjectStatusUpdate(){
        ResponseEntity<List<Project>> projectsResponse=projectService.updateExpiredProjects();
        List<Project> projects=null;
        if(projectsResponse.getStatusCode()== HttpStatus.OK){
            projects=projectsResponse.getBody();
        }
        for(Project p:projects){
            if(p.getStatus()== Status.OPEN_FOR_APPLICATIONS){
                p.setStatus(Status.CANCELED);
                System.out.println(p.getTitle()  +" is canceled" );
            }
        }
        System.out.println("Executed On " + LocalDateTime.now());
        projectDao.saveAll(projects);

    }
}
