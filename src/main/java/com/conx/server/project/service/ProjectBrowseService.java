package com.conx.server.project.service;

import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.domain.file.domain.File;
import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.domain.file.repository.FileRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectQuestion;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ProjectBrowseSort;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.dto.response.ProjectQuestionResponse;
import com.conx.server.project.repository.ProjectQuestionRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectBrowseService {

    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserFinder userFinder;
    private final FileRepository fileRepository;
    private final ProjectQuestionRepository projectQuestionRepository;

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
            ).map(p -> ProjectBrowseResponse.from(p, false,false));
        } else {
            boolean isCrew =  customUserDetails.getAuthorities().stream().map(
                    GrantedAuthority::getAuthority).anyMatch("ROLE_CREW"::equals);

            if (isCrew){
                Crew c = userFinder.findActiveCrew(customUserDetails.getId());

                return findProjects(
                        normalizedKeyword,
                        category,
                        projectType,
                        startDate,
                        endDate,
                        browseSort,
                        pageable
                ).map(p -> ProjectBrowseResponse.from(p, true, isBookmarked(p, c)));
            } else {
                return findProjects(
                        normalizedKeyword,
                        category,
                        projectType,
                        startDate,
                        endDate,
                        browseSort,
                        pageable
                ).map(p -> ProjectBrowseResponse.from(p, false, false));
            }


        }
    }

    private UserRole findUserRole(String authority) {
        for (UserRole role : UserRole.values()) {
            if (role.getRole().equals(authority)) {
                return role;
            }
        }

        throw new CustomException(ErrorCode.FORBIDDEN);
    }

    private UserRole getUserRole(
            CustomUserDetails userDetails
    ) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::findUserRole)
                .findFirst()
                .orElseThrow(() ->
                        new CustomException(ErrorCode.FORBIDDEN)
                );
    }

    private boolean isWriter(
            ProjectQuestion question,
            CustomUserDetails userDetails,
            UserRole userRole
    ) {
        return question.getWriterRole() == userRole
                && question.getWriterId().equals(userDetails.getId());
    }

    private boolean isProjectCompany(
            Project project,
            CustomUserDetails userDetails,
            UserRole userRole
    ) {
        return userRole == UserRole.COMPANY
                && project.getCompany().getId() == userDetails.getId();
    }

    private boolean isAdmin(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(
                GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals);
    }

    private boolean canViewSecret(
            ProjectQuestion question,
            Project project,
            CustomUserDetails userDetails
    ) {
        UserRole userRole = getUserRole(userDetails);

        return isAdmin(userDetails.getAuthorities())
                || isWriter(question, userDetails, userRole)
                || isProjectCompany(project, userDetails, userRole);
    }

    private boolean canViewQuestion(
            ProjectQuestion question,
            Project project,
            CustomUserDetails userDetails
    ) {
        if (!question.isSecret()) {
            return true;
        }

        return canViewSecret(question, project, userDetails);
    }

    @Transactional
    public ProjectBrowseDetailResponse getProjectDetail(Long projectId,
                                                        int page,
                                                        int size,
                                                        boolean mine,
                                                        CustomUserDetails userDetails) {
        Project project = projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.increaseViews();
        List<String> fileLinks = project.getFileLinks();
        List<File> files = fileRepository.findAllByUrlIn(fileLinks);
        List<FileResponseDTO> fileResponseDTOS = files.stream().map(FileResponseDTO::from).toList();

        Pageable pageable = PageRequest.of(page, size);
        UserRole userRole = getUserRole(userDetails);

        Page<ProjectQuestion> questions;

        if (mine) {
            if(userDetails == null) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }

            questions = projectQuestionRepository.findAllByProjectIdAndWriterIdAndWriterRoleOrderByIdDesc(
                            project.getId(), userDetails.getId(), userRole, pageable);
        } else {
            questions = projectQuestionRepository.findAllByProjectIdOrderByIdDesc(project.getId(),pageable);
        }

        Page<ProjectQuestionResponse> questionResponses = questions.map(question -> {
            boolean canView = canViewQuestion(question, project, userDetails);
            return ProjectQuestionResponse.from(question, canView);
        });

        return ProjectBrowseDetailResponse.from(project, fileResponseDTOS, questionResponses);
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