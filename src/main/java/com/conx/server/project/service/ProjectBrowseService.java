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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectBrowseService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<ProjectBrowseResponse> getProjects(
            String keyword,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            ProjectBrowseSort sort
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);
        ProjectBrowseSort browseSort = getOrDefault(sort, ProjectBrowseSort.RECENT);

        return findProjects(
                normalizedKeyword,
                category,
                projectType,
                startDate,
                endDate,
                browseSort
        )
                .stream()
                .map(ProjectBrowseResponse::from)
                .toList();
    }

    @Transactional
    public ProjectBrowseDetailResponse getProjectDetail(Long projectId) {
        Project project = projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.increaseViews();

        return ProjectBrowseDetailResponse.from(project);
    }

    private List<Project> findProjects(
            String keyword,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            ProjectBrowseSort sort
    ) {
        if (sort == ProjectBrowseSort.POPULAR) {
            return projectRepository.findBrowseProjectsOrderByPopular(
                    keyword,
                    category,
                    projectType,
                    startDate,
                    endDate
            );
        }

        if (sort == ProjectBrowseSort.RECOMMENDED) {
            return projectRepository.findBrowseProjectsOrderByRecommended(
                    keyword,
                    category,
                    projectType,
                    startDate,
                    endDate
            );
        }

        return projectRepository.findBrowseProjectsOrderByRecent(
                keyword,
                category,
                projectType,
                startDate,
                endDate
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