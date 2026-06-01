package com.conx.server.project.repository;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Long> {

    Optional<ProjectSubmission> findTopByProjectIdOrderByIdDesc(Long projectId);

    @Query("""
    select ps
    from ProjectSubmission ps
    where ps.project = :project
    """)
    Optional<ProjectSubmission> findCrewProjectSubmissionDTOByProject(
            @Param("project") Project project
    );
}