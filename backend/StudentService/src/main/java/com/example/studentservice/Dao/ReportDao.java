package com.example.studentservice.Dao;

import com.example.studentservice.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportDao extends JpaRepository<Report,Integer> {
    @Query("SELECT r FROM Report r WHERE r.studentProject.projectId = :projectId")
    Optional<Report> findReportByProjectId(@Param("projectId") int projectId);
}
