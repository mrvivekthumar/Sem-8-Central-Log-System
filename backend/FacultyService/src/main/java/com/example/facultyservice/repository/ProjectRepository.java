package com.example.facultyservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.facultyservice.domain.Project;
import com.example.facultyservice.domain.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

        @Query("SELECT p FROM Project p WHERE p.faculty.id = :facultyId")
        List<Project> findByFacultyId(@Param("facultyId") Long facultyId);

        List<Project> findByStatus(String status);

        @Query("SELECT p FROM Project p WHERE p.applicationDeadline >= :thresoldTime AND p.status = :status")
        List<Project> findVisibleProjects(@Param("thresoldTime") LocalDateTime localDateTime,
                        @Param("status") Status status);

        @Query("SELECT p FROM Project p WHERE p.status = :status AND p.applicationDeadline <= :thresholdTime")
        List<Project> findExpiredProjects(@Param("status") Status status,
                        @Param("thresholdTime") LocalDateTime thresholdTime);

        @Query("SELECT p FROM Project p WHERE p.faculty.id = :facultyId AND p.id IN :projectIds")
        List<Project> findByFacultyIdAndProjectIds(@Param("facultyId") int facultyId,
                        @Param("projectIds") List<Integer> projectIds);

        @Query("SELECT p FROM Project p WHERE p.id IN :projectIds")
        List<Project> findByProjectIds(@Param("projectIds") List<Integer> projectIds);

        @Query("SELECT count(p) FROM Project p where p.status = :status")
        Optional<Integer> findTotalApprovedProjects(@Param("status") Status status);
}