package com.example.studentservice.Service;

import com.example.studentservice.Vo.PersonalProjectDTO;
import com.example.studentservice.Model.PersonalProject;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Dao.PersonalProjectDao;
import com.example.studentservice.Dao.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonalProjectService {

    @Autowired
    private PersonalProjectDao personalProjectRepository;

    @Autowired
    private StudentDao studentRepository;

    // ✅ Create a new personal project
    public PersonalProjectDTO createPersonalProject(PersonalProjectDTO dto, int studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        PersonalProject project = new PersonalProject();
        project.setName(dto.getName());
        project.setDescreption(dto.getDescreption());
        project.setProjectLink(dto.getProjectLink());
        project.setStudent(student);

        PersonalProject savedProject = personalProjectRepository.save(project);

        return new PersonalProjectDTO(savedProject.getPersonalProjectId(), savedProject.getName(),
                savedProject.getDescreption(), savedProject.getProjectLink());
    }

    // ✅ Get all personal projects for a student
    public List<PersonalProjectDTO> getAllPersonalProjects(int studentId) {
        List<PersonalProject> projects = personalProjectRepository.findByStudent_StudentId(studentId);

        return projects.stream().map(project -> new PersonalProjectDTO(
                        project.getPersonalProjectId(), project.getName(), project.getDescreption(), project.getProjectLink()))
                .collect(Collectors.toList());
    }

    // ✅ Get a personal project by ID
    public PersonalProjectDTO getPersonalProjectById(int projectId) {
        PersonalProject project = personalProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return new PersonalProjectDTO(project.getPersonalProjectId(), project.getName(),
                project.getDescreption(), project.getProjectLink());
    }

    // ✅ Update a personal project
    public PersonalProjectDTO updatePersonalProject(int projectId, PersonalProjectDTO dto) {
        PersonalProject project = personalProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(dto.getName());
        project.setDescreption(dto.getDescreption());
        project.setProjectLink(dto.getProjectLink());

        PersonalProject updatedProject = personalProjectRepository.save(project);

        return new PersonalProjectDTO(updatedProject.getPersonalProjectId(), updatedProject.getName(),
                updatedProject.getDescreption(), updatedProject.getProjectLink());
    }

    // ✅ Delete a personal project
    public void deletePersonalProject(int projectId) {
        personalProjectRepository.deleteById(projectId);
    }
}
