package com.example.studentservice.Dao;

import com.example.studentservice.Model.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentProjectDao extends JpaRepository<StudentProject,Integer> {

    boolean existsByStudent_StudentIdAndProjectId(int projectId,int studentId);
}
