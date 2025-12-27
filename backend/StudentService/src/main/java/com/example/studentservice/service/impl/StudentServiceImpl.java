package com.example.studentservice.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Override
    public StudentDashboardDTO getDashboard(String studentId) {
        logger.info("Fetching dashboard data for student: {}", studentId);

        try {
            // ✅ FIXED: Use Optional-based repository method
            Student student = studentRepository.findByStudentId(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

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

    @Override
    public StudentProfileDTO getProfile(String studentId) {
        logger.info("Fetching profile for student: {}", studentId);

        try {
            // ✅ FIXED: Use Optional-based repository method
            Student student = studentRepository.findByStudentId(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

            StudentProfileDTO profile = new StudentProfileDTO();
            // ✅ FIXED: studentId is already String
            profile.setStudentId(studentId);
            profile.setName(student.getName());
            profile.setEmail(student.getEmail());
            // ✅ FIXED: Use getPhone() which now works
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
            // ✅ FIXED: Use Optional-based repository method
            Student student = studentRepository.findByStudentId(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

            student.setName(profileDTO.getName());
            student.setEmail(profileDTO.getEmail());
            // ✅ FIXED: Use setPhone() which now works
            student.setPhone(profileDTO.getPhone());

            Student updated = studentRepository.save(student);

            StudentProfileDTO updatedProfile = new StudentProfileDTO();
            // ✅ FIXED: studentId is already String
            updatedProfile.setStudentId(studentId);
            updatedProfile.setName(updated.getName());
            updatedProfile.setEmail(updated.getEmail());
            // ✅ FIXED: Use getPhone() which now works
            updatedProfile.setPhone(updated.getPhone());

            logger.info("Profile updated successfully for student: {}", studentId);
            return updatedProfile;

        } catch (Exception e) {
            logger.error("Error updating profile for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }
}
