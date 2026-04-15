package com.example.facultyservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.facultyservice.client.AuthInterface;
import com.example.facultyservice.client.StudentInterface;
import com.example.facultyservice.entity.Faculty;
import com.example.facultyservice.entity.Project;
import com.example.facultyservice.entity.Status;
import com.example.facultyservice.notification.model.NotificationRequest;
import com.example.facultyservice.notification.model.NotificationType;
import com.example.facultyservice.notification.model.ReceiverType;
import com.example.facultyservice.notification.model.SenderType;
import com.example.facultyservice.notification.service.NotificationService;
import com.example.facultyservice.repository.FacultyRepository;
import com.example.facultyservice.repository.ProjectRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentInterface studentInterface;

    @Autowired
    private AuthInterface authInterface;

    @PostConstruct
    public void init() {
        log.info("ProjectService initialized and ready");
    }

    /**
     * Get all projects
     */
    public List<Project> getAllProjects() {
        log.info("Service: Fetching all projects");
        List<Project> projects = projectRepository.findAll();
        log.info("Service: Found {} projects", projects.size());
        return projects;
    }

    /**
     * Get project by ID
     */
    public Optional<Project> getProjectById(Integer id) {
        log.info("Service: Fetching project by ID: {}", id);
        return projectRepository.findById(id);
    }

    /**
     * Get all projects for a specific faculty
     */
    public List<Project> getProjectsByFacultyId(Long facultyId) {
        log.info("Service: Fetching projects for faculty ID: {}", facultyId);
        List<Project> projects = projectRepository.findByFacultyId(facultyId);
        log.info("Service: Found {} projects for faculty {}", projects.size(), facultyId);
        return projects;
    }

    /**
     * Get projects by status
     */
    public List<Project> getProjectsByStatus(String status) {
        log.info("Service: Fetching projects with status: {}", status);
        return projectRepository.findByStatus(status);
    }

    /**
     * Get visible projects (open for applications and not past deadline)
     */
    public List<Project> getVisibleProjects() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Service: Fetching visible projects at {}", now);

        List<Project> visibleProjects = projectRepository.findVisibleProjects(now, Status.OPEN_FOR_APPLICATIONS);

        log.info("Service: Found {} visible projects", visibleProjects.size());
        return visibleProjects;
    }

    /**
     * Create a new project
     */
    public Project createProject(Project project, Integer facultyId) {
        log.info("Service: Creating new project '{}' for faculty ID: {}", project.getTitle(), facultyId);

        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);

        // Auto-register faculty from Auth Service if not found locally
        if (faculty == null) {
            log.info("Service: Faculty not found locally with ID: {}, fetching from Auth Service...", facultyId);
            try {
                ResponseEntity<Map<String, Object>> authResponse = authInterface.getUserById(Long.valueOf(facultyId));
                if (authResponse.getStatusCode().is2xxSuccessful() && authResponse.getBody() != null) {
                    Map<String, Object> userData = authResponse.getBody();
                    Faculty newFaculty = new Faculty();
                    newFaculty.setFId(facultyId);
                    newFaculty.setEmail((String) userData.get("email"));
                    newFaculty.setName((String) userData.get("name"));
                    faculty = facultyRepository.save(newFaculty);
                    log.info("Service: Auto-created faculty - ID: {}, Email: {}", faculty.getFId(), faculty.getEmail());
                } else {
                    throw new RuntimeException("Faculty not found with ID: " + facultyId);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.error("Service: Failed to fetch faculty from Auth Service: {}", e.getMessage());
                throw new RuntimeException("Faculty not found with ID: " + facultyId);
            }
        }

        project.setFaculty(faculty);
        project.setDate(LocalDateTime.now());
        project.setStatus(Status.OPEN_FOR_APPLICATIONS);

        Project savedProject = projectRepository.save(project);
        log.info("Service: Project created with ID: {}, status: {}, applicationDeadline: {}",
                savedProject.getProjectId(),
                savedProject.getStatus(),
                savedProject.getApplicationDeadline());

        // Send notification to all students about the new project
        try {
            ResponseEntity<List<Integer>> response = studentInterface.getAllStudentIds();
            List<Integer> studentIds = response.getBody();
            if (studentIds != null && !studentIds.isEmpty()) {
                List<String> receiverIds = studentIds.stream()
                        .map(String::valueOf)
                        .toList();

                NotificationRequest notifRequest = new NotificationRequest();
                notifRequest.setSenderId(String.valueOf(facultyId));
                notifRequest.setSenderType(SenderType.FACULTY);
                notifRequest.setReceiverType(ReceiverType.STUDENT);
                notifRequest.setNotificationType(NotificationType.PROJECT_CREATION);
                notifRequest.setTitle("New Project Available");
                notifRequest.setMessage("A new project \"" + savedProject.getTitle()
                        + "\" has been posted by " + faculty.getName()
                        + ". Check it out and apply!");

                notificationService.sendNotificationToMultipleReceivers(notifRequest, receiverIds);
                log.info("Service: Notifications sent to {} students for project '{}'",
                        receiverIds.size(), savedProject.getTitle());
            }
        } catch (Exception e) {
            log.error("Service: Failed to send project creation notifications: {}", e.getMessage());
            // Don't fail the project creation if notifications fail
        }

        return savedProject;
    }

    /**
     * Get confirmed projects for a faculty (status = IN_PROGRESS)
     */
    public List<Project> getConfirmedProjectsByFacultyId(Long facultyId) {
        log.info("Service: Fetching confirmed projects for faculty ID: {}", facultyId);
        List<Project> projects = projectRepository.findByFacultyIdAndStatus(facultyId, Status.IN_PROGRESS);
        log.info("Service: Found {} confirmed projects for faculty {}", projects.size(), facultyId);
        return projects;
    }

    /**
     * Get approved projects for a faculty (status = APPROVED)
     */
    public List<Project> getApprovedProjectsByFacultyId(Long facultyId) {
        log.info("Service: Fetching approved projects for faculty ID: {}", facultyId);
        List<Project> projects = projectRepository.findByFacultyIdAndStatus(facultyId, Status.APPROVED);
        log.info("Service: Found {} approved projects for faculty {}", projects.size(), facultyId);
        return projects;
    }

    /**
     * Update an existing project
     */
    public Project updateProject(Integer id, Project projectDetails) {
        log.info("Service: Updating project ID: {}", id);

        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        if (projectDetails.getTitle() != null) {
            existingProject.setTitle(projectDetails.getTitle());
        }
        if (projectDetails.getDescription() != null) {
            existingProject.setDescription(projectDetails.getDescription());
        }
        if (projectDetails.getStatus() != null) {
            existingProject.setStatus(projectDetails.getStatus());
        }
        if (projectDetails.getDeadline() != null) {
            existingProject.setDeadline(projectDetails.getDeadline());
        }
        if (projectDetails.getApplicationDeadline() != null) {
            existingProject.setApplicationDeadline(projectDetails.getApplicationDeadline());
        }
        if (projectDetails.getMaxStudents() != null) {
            existingProject.setMaxStudents(projectDetails.getMaxStudents());
        }
        if (projectDetails.getSkills() != null) {
            existingProject.setSkills(projectDetails.getSkills());
        }

        Project updatedProject = projectRepository.save(existingProject);
        log.info("Service: Project updated successfully - ID: {}", id);
        return updatedProject;
    }

    /**
     * Delete a project
     */
    public void deleteProject(Integer id) {
        log.info("Service: Deleting project ID: {}", id);

        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with ID: " + id);
        }

        projectRepository.deleteById(id);
        log.info("Service: Project deleted successfully - ID: {}", id);
    }

    /**
     * Get projects by IDs (bulk)
     */
    public List<Project> getProjectsByIds(List<Integer> projectIds) {
        log.info("Service: Fetching projects by IDs: {}", projectIds);
        return projectRepository.findByProjectIds(projectIds);
    }

    /**
     * Get project count for a faculty
     */
    public long getProjectCountByFacultyId(Long facultyId) {
        return projectRepository.findByFacultyId(facultyId).size();
    }
}
