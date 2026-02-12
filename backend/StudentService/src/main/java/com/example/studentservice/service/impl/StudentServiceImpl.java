package com.example.studentservice.service.impl;

import com.example.studentservice.client.AuthInterface;
import com.example.studentservice.domain.Student;
import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.dto.StudentDashboardDTO;
import com.example.studentservice.dto.StudentProfileDTO;
import com.example.studentservice.repository.StudentProjectRepository;
import com.example.studentservice.repository.StudentRepository;
import com.example.studentservice.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private AuthInterface authInterface;

    @Override
    public StudentDashboardDTO getDashboard(String studentId) {
        logger.info("Fetching dashboard data for student: {}", studentId);

        try {
            Integer studentIdInt = Integer.parseInt(studentId);
            Student student = getOrCreateStudent(studentIdInt);

            // ✅ FIXED: Use correct repository method
            List<StudentProject> projects = studentProjectRepository.findByStudentId(studentId);

            StudentDashboardDTO dashboard = new StudentDashboardDTO();
            dashboard.setStudentId(studentId);
            dashboard.setStudentName(student.getName());
            dashboard.setTotalProjects(projects.size());
            dashboard.setProjects(projects);

            logger.info("Dashboard data fetched successfully for student: {}", studentId);
            return dashboard;

        } catch (Exception e) {
            logger.error("Error fetching dashboard for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<StudentProject> getProjects(String studentId) {
        logger.info("Fetching projects for student: {}", studentId);

        try {
            // ✅ FIXED: Use correct repository method
            List<StudentProject> projects = studentProjectRepository.findByStudentId(studentId);
            logger.info("Found {} projects for student: {}", projects.size(), studentId);
            return projects;

        } catch (Exception e) {
            logger.error("Error fetching projects for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public StudentProject getProjectById(Long projectId) {
        logger.info("Fetching project by ID: {}", projectId);

        try {
            // ✅ FIXED: Use Long type for findById
            StudentProject project = studentProjectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

            logger.info("Project found: {} (ID: {})", project.getProjectName(), projectId);
            return project;

        } catch (Exception e) {
            logger.error("Error fetching project {}: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Helper method to get or create student from Auth Service
     * Auto-creates student record if not exists locally
     */
    private Student getOrCreateStudent(Integer studentIdInt) {
        // First try to find locally
        Optional<Student> existingStudent = studentRepository.findByStudentId(studentIdInt);
        if (existingStudent.isPresent()) {
            return existingStudent.get();
        }

        // Student not found locally - fetch from Auth Service and auto-create
        logger.info("Student not found locally, fetching from Auth Service: {}", studentIdInt);
        try {
            ResponseEntity<Map<String, Object>> authResponse = authInterface.getUserById(Long.valueOf(studentIdInt));

            if (authResponse.getStatusCode().is2xxSuccessful() && authResponse.getBody() != null) {
                Map<String, Object> userData = authResponse.getBody();

                // Create new student from auth user data
                Student newStudent = new Student();
                newStudent.setStudentId(studentIdInt);
                newStudent.setEmail((String) userData.get("email"));
                newStudent.setName((String) userData.get("name"));
                newStudent.setPhone((String) userData.get("phone"));

                Student savedStudent = studentRepository.save(newStudent);
                logger.info("Auto-created student profile - ID: {}, Email: {}", savedStudent.getStudentId(),
                        savedStudent.getEmail());
                return savedStudent;
            } else {
                throw new RuntimeException("Student not found in Auth Service: " + studentIdInt);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch/create student from Auth Service: {}", e.getMessage());
            throw new RuntimeException("Student not found: " + studentIdInt);
        }
    }

    @Override
    public StudentProfileDTO getProfile(String studentId) {
        logger.info("Fetching profile for student: {}", studentId);

        try {
            Integer studentIdInt = Integer.parseInt(studentId);
            Student student = getOrCreateStudent(studentIdInt);

            StudentProfileDTO profile = new StudentProfileDTO();
            profile.setStudentId(studentId);
            profile.setName(student.getName());
            profile.setEmail(student.getEmail());
            profile.setPhone(student.getPhone());

            logger.info("Profile fetched successfully for student: {}", studentId);
            return profile;

        } catch (Exception e) {
            logger.error("Error fetching profile for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public StudentProfileDTO updateProfile(String studentId, StudentProfileDTO profileDTO) {
        logger.info("Updating profile for student: {}", studentId);

        try {
            Integer studentIdInt = Integer.parseInt(studentId);
            Student student = getOrCreateStudent(studentIdInt);

            student.setName(profileDTO.getName());
            student.setEmail(profileDTO.getEmail());
            student.setPhone(profileDTO.getPhone());

            Student updated = studentRepository.save(student);

            StudentProfileDTO updatedProfile = new StudentProfileDTO();
            updatedProfile.setStudentId(studentId);
            updatedProfile.setName(updated.getName());
            updatedProfile.setEmail(updated.getEmail());
            updatedProfile.setPhone(updated.getPhone());

            logger.info("Profile updated successfully for student: {}", studentId);
            return updatedProfile;

        } catch (Exception e) {
            logger.error("Error updating profile for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<StudentProject> getCompletedProjects(int studentId) {
        logger.info("Service: Fetching completed projects for student {}", studentId);
        try {
            List<StudentProject> completedProjects = studentProjectRepository
                    .findCompletedProjectsByStudentId(studentId);
            logger.info("Service: Found {} completed projects for student {}", completedProjects.size(), studentId);
            return completedProjects;
        } catch (Exception e) {
            logger.error("Error fetching completed projects for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }
}
