package com.conx.server.project.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ProjectBrowseSort;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.types.Industry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProjectBrowseService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public Page<ProjectBrowseResponse> getProjects(
            String keyword,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            ProjectBrowseSort sort,
            int page,
            int size
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);
        ProjectBrowseSort browseSort = getOrDefault(sort, ProjectBrowseSort.RECENT);
        Pageable pageable = PageRequest.of(page, size);

        return findProjects(
                normalizedKeyword,
                category,
                projectType,
                startDate,
                endDate,
                browseSort,
                pageable
        ).map(ProjectBrowseResponse::from);
    }

    @Transactional
    public ProjectBrowseDetailResponse getProjectDetail(Long projectId) {
        Project project = projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.increaseViews();

        return ProjectBrowseDetailResponse.from(project);
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