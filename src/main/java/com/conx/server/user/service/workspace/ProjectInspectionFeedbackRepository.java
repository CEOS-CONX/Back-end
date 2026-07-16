package com.conx.server.user.service.workspace;

import com.conx.server.project.domain.ProjectInspectionFeedback;
import com.conx.server.project.domain.ProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProjectInspectionFeedbackRepository extends JpaRepository<ProjectInspectionFeedback, Long> {
    ProjectInspectionFeedback findBySubmission(ProjectSubmission submission);
}
