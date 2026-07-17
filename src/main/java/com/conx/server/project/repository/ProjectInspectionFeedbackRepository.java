package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectInspectionFeedback;
import com.conx.server.project.domain.ProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectInspectionFeedbackRepository
        extends JpaRepository<ProjectInspectionFeedback, Long> {

    ProjectInspectionFeedback findBySubmission(
            ProjectSubmission submission
    );
}