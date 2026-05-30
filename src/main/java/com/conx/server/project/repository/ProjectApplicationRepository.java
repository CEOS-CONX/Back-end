package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {

    List<ProjectApplication> findAllByProjectId(Long projectId);

    Optional<ProjectApplication> findByIdAndProjectId(Long applicationId, Long projectId);

    List<ProjectApplication> findAllByProjectIdAndStatus(Long projectId, ProjectApplicationStatus status);
}