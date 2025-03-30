package com.example.studentservice.Dao;

import com.example.studentservice.Model.StudentProject;
import com.example.studentservice.Vo.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProjectDao extends JpaRepository<StudentProject,Integer> {

    boolean existsByStudent_StudentIdAndProjectId(int projectId,int studentId);
    @Query("SELECT sp.student.studentId FROM StudentProject sp WHERE sp.projectId = :projectId")
    List<Integer> findStudent_StudentIdByProjectId(int projectId);
    @Query("SELECT COUNT(sp) FROM StudentProject sp WHERE sp.projectId = :projectId")
    int countStudentsByProjectId(@Param("projectId") int projectId);

    List<StudentProject> findByProjectId(int projectId);
    @Query("SELECT DISTINCT sp.projectId FROM StudentProject sp")
    List<Integer> findDistinctByProjectId();

    List<StudentProject> findByStudent_StudentId(int studentId);

    StudentProject findByStudent_StudentIdAndProjectId(int studentId, int projectId);

    @Query("SELECT sp.projectId FROM StudentProject sp where sp.student.studentId= :studentId")
    List<Integer> findProjectIdsByStudent_StudentId(@Param("studentId") int studentId);
    @Query("SELECT  MAX(sp.preference) FROM  StudentProject sp where sp.student.studentId=:studentId")
    Integer findMaxPreferenceByStudentId(@Param("studentId") int studentId);
    @Query("SELECT sp FROM StudentProject sp WHERE sp.student.studentId = :studentId ORDER BY sp.preference ASC")
    List<StudentProject> findProjectsByStudentOrdered(@Param("studentId") int studentId);
    @Query("SELECT sp.projectId FROM StudentProject sp WHERE sp.student.studentId = :studentId ORDER BY sp.preference ASC")
    List<Integer> findProjectIdsByStudentOrdered(@Param("studentId") int studentId);


    List<StudentProject> findByProjectIdAndStudent_StudentIdIn(int projectId, List<Integer> studentIds);

    @Query("SELECT sp.student.studentId FROM StudentProject sp WHERE sp.projectId = :projectId AND sp.status = 'APPROVED'")
    List<Integer> findStudentIdByApproved(@Param("projectId") int projectId);

    @Query("SELECT sp.projectId FROM StudentProject sp WHERE sp.student.studentId = :studentId AND sp.status = :status")
    Optional<List<Integer>> findProjectIdByStudent_StudentIdAndStatus(int studentId, Status status);
    @Query("SELECT  sp.student.studentId FROM StudentProject sp WHERE sp.projectId = :projectId AND sp.status = :status")
    List<Integer> findStudentIdByProjectIdAndStatus(@Param("projectId") int projectId, @Param("status") Status status);
    @Query("select sp.student.studentId from StudentProject sp where sp.projectId = :projectId and sp.status IN :status")
    List<Integer> findIdsCompletedStudentsByProjectId(@Param("projectId") int projectId, @Param("status") List<Status> status);
    @Query("SELECT sp FROM StudentProject sp WHERE sp.projectId = :projectId AND sp.status = :status")
    List<StudentProject> findByProjectIdAndStatus(@Param("projectId") int projectId, @Param("status") Status status);
}
