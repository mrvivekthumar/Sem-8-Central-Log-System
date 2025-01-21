package com.example.studentservice.Dao;

import com.example.studentservice.Model.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentProjectDao extends JpaRepository<StudentProject,Integer> {

    boolean existsByStudent_StudentIdAndProjectId(int projectId,int studentId);
    @Query("SELECT sp.student.studentId FROM StudentProject sp WHERE sp.projectId = :projectId")
    List<Integer> findStudent_StudentIdByProjectId(int projectId);
    @Query("SELECT COUNT(sp) FROM StudentProject sp WHERE sp.projectId = :projectId")
    int countStudentsByProjectId(@Param("projectId") int projectId);

    List<StudentProject> findByProjectId(int projectId);
}
