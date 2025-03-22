package com.example.facultyservice.Dao;

import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProjectDao extends JpaRepository<Project,Integer> {

    @Query("SELECT p FROM Project p WHERE p.faculty.f_id = :facultyId")
    List<Project> findByFacultyId(@Param("facultyId") int facultyId);
    @Query("SELECT p FROM Project p WHERE p.applicationDeadline >= :thresoldTime AND p.status = :status")
    List<Project> findVisibleProjects(@Param("thresoldTime") LocalDateTime localDateTime, @Param("status") Status status);

    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.applicationDeadline <= :thresholdTime")
    List<Project> findExpiredProjects(@Param("status") Status status, @Param("thresholdTime") LocalDateTime thresholdTime);
    @Query("SELECT p FROM Project p where p.faculty.f_id=:facultyId AND p.projectId in :projectIds")
    List<Project> findByFacultyIdAndProjectIds(int facultyId, List<Integer> projectIds);
    @Query("SELECT p FROM  Project p where p.projectId in :projectIds")
    List<Project> findByProjectIds(@Param("projectIds") List<Integer> projectIds);
    @Query("SELECT count(p) FROM Project p where p.status = :status")
    Optional<Integer> findTotalApprovedProjects(@Param("status") Status status);
}