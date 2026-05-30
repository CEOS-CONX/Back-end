package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Long> {

    Optional<ProjectSubmission> findTopByProjectIdOrderByIdDesc(Long projectId);
}