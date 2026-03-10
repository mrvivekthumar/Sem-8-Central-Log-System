package com.example.studentservice.service.impl;

import com.example.studentservice.client.AuthInterface;
import com.example.studentservice.client.FacultyInterface;
import com.example.studentservice.client.dto.Project;
import com.example.studentservice.domain.Student;
import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.dto.CompletedProjectDTO;
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

    @Autowired
    private FacultyInterface facultyInterface;

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

            StudentProfileDTO profile = mapToDTO(student);

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

            if (profileDTO.getName() != null)
                student.setName(profileDTO.getName());
            if (profileDTO.getEmail() != null)
                student.setEmail(profileDTO.getEmail());
            if (profileDTO.getPhone() != null)
                student.setPhone(profileDTO.getPhone());
            if (profileDTO.getBio() != null)
                student.setBio(profileDTO.getBio());
            if (profileDTO.getGithubProfileLink() != null)
                student.setGithubProfileLink(profileDTO.getGithubProfileLink());
            if (profileDTO.getLinkedInUrl() != null)
                student.setLinkedInUrl(profileDTO.getLinkedInUrl());
            if (profileDTO.getCgpa() != null)
                student.setCgpa(profileDTO.getCgpa());
            if (profileDTO.getImageUrl() != null)
                student.setImageUrl(profileDTO.getImageUrl());
            if (profileDTO.getSkills() != null)
                student.setSkills(profileDTO.getSkills());
            if (profileDTO.getSemesterNo() != null)
                student.setSemesterNo(profileDTO.getSemesterNo());
            if (profileDTO.getLocation() != null)
                student.setLocation(profileDTO.getLocation());
            if (profileDTO.getPortfolioLink() != null)
                student.setPortfolioLink(profileDTO.getPortfolioLink());

            Student updated = studentRepository.save(student);

            StudentProfileDTO updatedProfile = mapToDTO(updated);

            logger.info("Profile updated successfully for student: {}", studentId);
            return updatedProfile;

        } catch (Exception e) {
            logger.error("Error updating profile for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    private StudentProfileDTO mapToDTO(Student student) {
        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setStudentId(String.valueOf(student.getStudentId()));
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setBio(student.getBio());
        dto.setGithubProfileLink(student.getGithubProfileLink());
        dto.setLinkedInUrl(student.getLinkedInUrl());
        dto.setCgpa(student.getCgpa());
        dto.setImageUrl(student.getImageUrl());
        dto.setSkills(student.getSkills());
        dto.setSemesterNo(student.getSemesterNo());
        dto.setLocation(student.getLocation());
        dto.setPortfolioLink(student.getPortfolioLink());
        return dto;
    }

    @Override
    public List<CompletedProjectDTO> getCompletedProjects(int studentId) {
        logger.info("Service: Fetching completed projects for student {}", studentId);
        try {
            List<StudentProject> completedProjects = studentProjectRepository
                    .findCompletedProjectsByStudentId(studentId);
            logger.info("Service: Found {} completed projects for student {}", completedProjects.size(), studentId);

            // Fetch full project details from FacultyService
            List<Integer> projectIds = completedProjects.stream()
                    .map(StudentProject::getProjectId)
                    .toList();

            java.util.Map<Integer, Project> projectMap = new java.util.HashMap<>();
            if (!projectIds.isEmpty()) {
                try {
                    ResponseEntity<List<Project>> response = facultyInterface.getProjectsByIds(projectIds);
                    if (response.getBody() != null) {
                        for (Project p : response.getBody()) {
                            projectMap.put(p.getProjectId(), p);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Could not fetch project details from FacultyService: {}", e.getMessage());
                }
            }

            // Merge StudentProject with Project data
            return completedProjects.stream().map(sp -> {
                Project project = projectMap.get(sp.getProjectId());
                CompletedProjectDTO dto = CompletedProjectDTO.builder()
                        .applicationId(sp.getApplicationId())
                        .projectId(sp.getProjectId())
                        .projectName(sp.getProjectName())
                        .status(sp.getStatus() != null ? sp.getStatus().name() : "COMPLETED")
                        .applicationDate(sp.getApplicationDate())
                        .build();

                if (project != null) {
                    dto.setTitle(project.getTitle());
                    dto.setDescription(project.getDescription());
                    dto.setDomain(project.getDomain());
                    dto.setTeamSize(project.getTeamSize());
                    dto.setDuration(project.getDuration());
                    dto.setSkills(project.getSkills());
                    dto.setDeadline(project.getDeadline());
                    if (project.getFaculty() != null) {
                        dto.setFacultyName(project.getFaculty().getName());
                    }
                } else {
                    dto.setTitle(sp.getProjectName());
                }
                return dto;
            }).toList();
        } catch (Exception e) {
            logger.error("Error fetching completed projects for student {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateStudentsAvailability(int projectId) {
        logger.info("Service: Updating students availability for project {}", projectId);
        try {
            List<StudentProject> studentProjects = studentProjectRepository.findByProjectId(projectId);
            for (StudentProject sp : studentProjects) {
                Student student = sp.getStudent();
                if (student != null) {
                    student.setStudentAvaibility(com.example.studentservice.domain.StudentAvaibility.NOT_AVAILABLE);
                    studentRepository.save(student);
                    logger.debug("Updated availability for student: {}", student.getStudentId());
                }
            }
            logger.info("Service: Updated availability for {} students on project {}", studentProjects.size(),
                    projectId);
        } catch (Exception e) {
            logger.error("Error updating students availability for project {}: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Student> getStudentsByIds(List<Integer> studentIds) {
        logger.info("Service: Fetching students by IDs: {}", studentIds);
        try {
            List<Student> students = studentRepository.findAllByStudentId(studentIds);
            logger.info("Service: Found {} students", students.size());
            return students;
        } catch (Exception e) {
            logger.error("Error fetching students by IDs: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void makeStudentUnavailable(int studentId) {
        logger.info("Service: Making student {} unavailable", studentId);
        try {
            Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setStudentAvaibility(com.example.studentservice.domain.StudentAvaibility.NOT_AVAILABLE);
                studentRepository.save(student);
                logger.info("Service: Student {} marked as unavailable", studentId);
            } else {
                logger.warn("Service: Student {} not found", studentId);
                throw new RuntimeException("Student not found: " + studentId);
            }
        } catch (Exception e) {
            logger.error("Error making student {} unavailable: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Integer> getAllStudentIds() {
        logger.info("Service: Fetching all student IDs");
        List<Integer> ids = studentRepository.findAll().stream()
                .map(Student::getStudentId)
                .toList();
        logger.info("Service: Found {} student IDs", ids.size());
        return ids;
    }
}
