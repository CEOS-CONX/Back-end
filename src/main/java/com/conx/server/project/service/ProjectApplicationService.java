package com.conx.server.project.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.repository.CrewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectApplicationService {

    private final ProjectRepository projectRepository;
    private final CrewRepository crewRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    @Transactional
    public ProjectApplicationResponse applyProject(
            Long projectId,
            Long crewId,
            ProjectApplicationRequest request
    ) {
        Project project = projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        Crew crew = crewRepository.findByIdAndStatus(crewId, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        if (projectApplicationRepository.existsByProjectIdAndCrewId(projectId, crewId)) {
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        ProjectApplication application = ProjectApplication.create(
                project,
                crew,
                request.introduction(),
                request.proposal()
        );

        ProjectApplication savedApplication = projectApplicationRepository.save(application);

        return ProjectApplicationResponse.from(savedApplication);
    }

    @Transactional
    public void cancelApplication(Long projectId, Long crewId) {
        ProjectApplication application = projectApplicationRepository.findByProjectIdAndCrewId(projectId, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.isPending()) {
            throw new CustomException(ErrorCode.INVALID_APPLICATION_STATUS);
        }

        projectApplicationRepository.delete(application);
    }
}