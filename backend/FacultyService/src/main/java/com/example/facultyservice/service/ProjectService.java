package com.example.facultyservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.facultyservice.entity.Faculty;
import com.example.facultyservice.entity.Project;
import com.example.facultyservice.entity.Status;
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
        log.info("Service: Fetching visible projects");
        return projectRepository.findVisibleProjects(
                LocalDateTime.now(),
                Status.OPEN_FOR_APPLICATIONS);
    }

    /**
     * Create a new project
     */
    public Project createProject(Project project, Integer facultyId) {
        log.info("Service: Creating new project '{}' for faculty ID: {}", project.getTitle(), facultyId);

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + facultyId));

        project.setFaculty(faculty);
        project.setDate(LocalDateTime.now());
        if (project.getStatus() == null) {
            project.setStatus(Status.OPEN_FOR_APPLICATIONS);
        }

        Project savedProject = projectRepository.save(project);
        log.info("Service: Project created with ID: {}", savedProject.getProjectId());
        return savedProject;
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
