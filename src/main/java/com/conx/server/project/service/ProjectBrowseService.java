package com.conx.server.project.service;

import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.domain.file.domain.File;
import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.domain.file.repository.FileRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ProjectBrowseSort;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectBrowseService {

    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserFinder userFinder;
    private final FileRepository fileRepository;

    @Transactional(readOnly = true)
    protected boolean isBookmarked(Project p, Crew c){
        return projectBookmarkRepository.existsByCrewAndProject(c,p);
    }

    @Transactional(readOnly = true)
    public Page<ProjectBrowseResponse> getProjects(
            String keyword,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            ProjectBrowseSort sort,
            int page,
            int size,
            CustomUserDetails customUserDetails
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);
        ProjectBrowseSort browseSort = getOrDefault(sort, ProjectBrowseSort.RECENT);
        Pageable pageable = PageRequest.of(page, size);

        if (customUserDetails == null){
            return findProjects(
                    normalizedKeyword,
                    category,
                    projectType,
                    startDate,
                    endDate,
                    browseSort,
                    pageable
            ).map(p -> ProjectBrowseResponse.from(p,false));
        } else {
            Crew c = userFinder.findActiveCrew(customUserDetails.getId());

            return findProjects(
                    normalizedKeyword,
                    category,
                    projectType,
                    startDate,
                    endDate,
                    browseSort,
                    pageable
            ).map(p -> ProjectBrowseResponse.from(p, isBookmarked(p, c)));
        }
    }

    @Transactional
    public ProjectBrowseDetailResponse getProjectDetail(Long projectId) {
        Project project = projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.increaseViews();
        List<String> fileLinks = project.getFileLinks();
        List<File> files = fileRepository.findAllByUrlIn(fileLinks);
        List<FileResponseDTO> fileResponseDTOS = files.stream().map(
                FileResponseDTO::from
        ).toList();

        return ProjectBrowseDetailResponse.from(project, fileResponseDTOS);
    }

    private Page<Project> findProjects(
            String keyword,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            ProjectBrowseSort sort,
            Pageable pageable
    ) {
        if (sort == ProjectBrowseSort.POPULAR) {
            return projectRepository.findBrowseProjectsOrderByPopular(
                    keyword,
                    category,
                    projectType,
                    startDate,
                    endDate,
                    pageable
            );
        }

        if (sort == ProjectBrowseSort.RECOMMENDED) {
            return projectRepository.findBrowseProjectsOrderByRecommended(
                    keyword,
                    category,
                    projectType,
                    startDate,
                    endDate,
                    pageable
            );
        }

        return projectRepository.findBrowseProjectsOrderByRecent(
                keyword,
                category,
                projectType,
                startDate,
                endDate,
                pageable
        );
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }

    private <T> T getOrDefault(T newValue, T defaultValue) {
        if (newValue == null) {
            return defaultValue;
        }

        return newValue;
    }
}