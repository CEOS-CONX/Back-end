package com.conx.server.bookmark.service;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.bookmark.dto.response.ProjectBookmarkResponse;
import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.Project;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.repository.CrewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectBookmarkService {

    private final ProjectRepository projectRepository;
    private final CrewRepository crewRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;

    @Transactional
    public ProjectBookmarkResponse addBookmark(Long projectId, Long crewId) {
        Project project = projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        Crew crew = crewRepository.findByIdAndStatus(crewId, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        if (projectBookmarkRepository.existsByCrewIdAndProjectId(crewId, projectId)) {
            throw new CustomException(ErrorCode.PROJECT_BOOKMARK_ALREADY_EXISTS);
        }

        ProjectBookmark projectBookmark = ProjectBookmark.create(crew, project);
        projectBookmarkRepository.save(projectBookmark);

        return new ProjectBookmarkResponse(projectId, true);
    }

    @Transactional
    public ProjectBookmarkResponse removeBookmark(Long projectId, Long crewId) {
        ProjectBookmark projectBookmark = projectBookmarkRepository.findByCrewIdAndProjectId(crewId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_BOOKMARK_NOT_FOUND));

        projectBookmarkRepository.delete(projectBookmark);

        return new ProjectBookmarkResponse(projectId, false);
    }
}