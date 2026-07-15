package com.conx.server.project.repository;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Long> {

    Optional<ProjectSubmission> findTopByProjectIdOrderByIdDesc(Long projectId);

    Optional<ProjectSubmission> findByProject(Project project);

    Optional<ProjectSubmission> findByProjectAndStatus(Project project, ProjectSubmissionStatus status);

    Page<ProjectSubmission> findByProject(Project project, Pageable pageable);
}