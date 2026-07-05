package com.conx.server.project.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectQuestion;
import com.conx.server.project.dto.request.ProjectQuestionAnswerRequest;
import com.conx.server.project.dto.request.ProjectQuestionCreateRequest;
import com.conx.server.project.dto.response.ProjectQuestionDetailResponse;
import com.conx.server.project.dto.response.ProjectQuestionResponse;
import com.conx.server.project.repository.ProjectQuestionRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectQuestionService {

    private final ProjectRepository projectRepository;
    private final ProjectQuestionRepository projectQuestionRepository;
    private final UserFinder userFinder;

    @Transactional(readOnly = true)
    public Page<ProjectQuestionResponse> getQuestions(
            Long projectId,
            int page,
            int size,
            CustomUserDetails userDetails
    ) {
        Project project = findRecruitingProject(projectId);
        Pageable pageable = PageRequest.of(page, size);

        return projectQuestionRepository.findAllByProjectIdOrderByIdDesc(project.getId(), pageable)
                .map(question -> ProjectQuestionResponse.from(question, canViewSecret(question, project, userDetails)));
    }

    @Transactional(readOnly = true)
    public ProjectQuestionDetailResponse getQuestion(
            Long projectId,
            Long questionId,
            CustomUserDetails userDetails
    ) {
        Project project = findRecruitingProject(projectId);
        ProjectQuestion question = findQuestion(projectId, questionId);

        if (question.isSecret() && !canViewSecret(question, project, userDetails)) {
            throw new CustomException(ErrorCode.PROJECT_QUESTION_ACCESS_DENIED);
        }

        return ProjectQuestionDetailResponse.from(question);
    }

    @Transactional
    public ProjectQuestionDetailResponse createQuestion(
            Long projectId,
            CustomUserDetails userDetails,
            ProjectQuestionCreateRequest request
    ) {
        Project project = findRecruitingProject(projectId);
        UserRole writerRole = getUserRole(userDetails);

        ProjectQuestion question;
        if (writerRole == UserRole.CREW) {
            Crew crew = userFinder.findActiveCrew(userDetails.getId());
            question = ProjectQuestion.create(
                    project,
                    crew.getId(),
                    writerRole,
                    crew.getCrewName(),
                    request.content(),
                    request.secret()
            );
        } else if (writerRole == UserRole.COMPANY) {
            Company company = userFinder.findActiveCompany(userDetails.getId());
            question = ProjectQuestion.create(
                    project,
                    company.getId(),
                    writerRole,
                    company.getCompanyName(),
                    request.content(),
                    request.secret()
            );
        } else {
            throw new CustomException(ErrorCode.PROJECT_QUESTION_ACCESS_DENIED);
        }

        return ProjectQuestionDetailResponse.from(projectQuestionRepository.save(question));
    }

    @Transactional
    public ProjectQuestionDetailResponse answerQuestion(
            Long projectId,
            Long questionId,
            CustomUserDetails userDetails,
            ProjectQuestionAnswerRequest request
    ) {
        Project project = findRecruitingProject(projectId);
        ProjectQuestion question = findQuestion(projectId, questionId);

        if (!canAnswer(project, userDetails)) {
            throw new CustomException(ErrorCode.PROJECT_QUESTION_ANSWER_ACCESS_DENIED);
        }

        question.answer(request.answerContent());

        return ProjectQuestionDetailResponse.from(question);
    }

    private Project findRecruitingProject(Long projectId) {
        return projectRepository.findRecruitingProjectById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private ProjectQuestion findQuestion(Long projectId, Long questionId) {
        return projectQuestionRepository.findByIdAndProjectId(questionId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_QUESTION_NOT_FOUND));
    }

    private boolean canViewSecret(ProjectQuestion question, Project project, CustomUserDetails userDetails) {
        UserRole userRole = getUserRole(userDetails);

        return isAdmin(userRole)
                || isWriter(question, userDetails, userRole)
                || isProjectCompany(project, userDetails, userRole);
    }

    private boolean canAnswer(Project project, CustomUserDetails userDetails) {
        UserRole userRole = getUserRole(userDetails);

        return isAdmin(userRole) || isProjectCompany(project, userDetails, userRole);
    }

    private boolean isWriter(ProjectQuestion question, CustomUserDetails userDetails, UserRole userRole) {
        return question.getWriterRole() == userRole && question.getWriterId().equals(userDetails.getId());
    }

    private boolean isProjectCompany(Project project, CustomUserDetails userDetails, UserRole userRole) {
        return userRole == UserRole.COMPANY && project.getCompany().getId() == userDetails.getId();
    }

    private boolean isAdmin(UserRole userRole) {
        return userRole == UserRole.ADMIN;
    }

    private UserRole getUserRole(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::findUserRole)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));
    }

    private UserRole findUserRole(String authority) {
        for (UserRole role : UserRole.values()) {
            if (role.getRole().equals(authority)) {
                return role;
            }
        }
        throw new CustomException(ErrorCode.FORBIDDEN);
    }
}
