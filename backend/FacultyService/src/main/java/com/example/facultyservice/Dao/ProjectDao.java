package com.example.facultyservice.Dao;

import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectDao extends JpaRepository<Project,Integer> {

    @Query("SELECT p FROM Project p WHERE p.faculty.f_id = :facultyId")
    List<Project> findByFacultyId(@Param("facultyId") int facultyId);
    @Query("SELECT p FROM Project p WHERE p.date >= :thresoldTime AND p.status = :status")
    List<Project> findVisibleProjects(@Param("thresoldTime") LocalDateTime localDateTime, @Param("status") Status status);

    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.date <= :thresholdTime")
    List<Project> findExpiredProjects(@Param("status") Status status, @Param("thresholdTime") LocalDateTime thresholdTime);

}