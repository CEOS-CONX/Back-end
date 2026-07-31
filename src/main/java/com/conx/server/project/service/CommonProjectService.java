package com.conx.server.project.service;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.domain.file.repository.FileRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectInspectionFeedback;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.project.repository.ProjectInspectionFeedbackRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.repository.ProjectSettlementRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.company.response.*;
import com.conx.server.user.service.common.UserFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class CommonProjectService {
    private final UserFinder userFinder;
    private final ProjectSettlementRepository projectSettlementRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final ProjectInspectionFeedbackRepository projectInspectionFeedbackRepository;

    public CommonProjectService(UserFinder userFinder, ProjectSettlementRepository projectSettlementRepository,
                                ProjectSubmissionRepository projectSubmissionRepository, ProjectRepository projectRepository, FileRepository fileRepository, ProjectInspectionFeedbackRepository projectInspectionFeedbackRepository) {
        this.userFinder = userFinder;
        this.projectSettlementRepository = projectSettlementRepository;
        this.projectSubmissionRepository = projectSubmissionRepository;
        this.projectRepository = projectRepository;
        this.fileRepository = fileRepository;
        this.projectInspectionFeedbackRepository = projectInspectionFeedbackRepository;
    }

    /**
     * 결과물 및 피드백 상세 조회
     */
    @Transactional(readOnly = true)
    public ProjectInspectionWrapperDTO getProjectReviewDetail(
            String email,
            Long projectId,
            Long submissionId
    ) {
        Project project = findAndVerifyProject(email, projectId);

        ProjectSubmission submission =
                projectSubmissionRepository.findByIdAndProjectId(
                        project.getId(),
                        submissionId
                ).orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        DetailedProjectResponseDTO common = createCommon(project);

        List<FileResponseDTO> submissionFiles =
                fileRepository
                        .findByUrlIn(
                                submission.getFileLinks()
                        )
                        .stream()
                        .map(
                                FileResponseDTO::from
                        )
                        .toList();

        ProjectSubmissionWrapperDTO submissionDTO =
                ProjectSubmissionWrapperDTO.from(
                        submission,
                        submissionFiles,
                        submission.getAdditionalLinks()
                );

        ProjectInspectionFeedback feedback =
                projectInspectionFeedbackRepository
                        .findBySubmission(
                                submission
                        );

        if (feedback == null) {
            return ProjectInspectionWrapperDTO.from(
                    common,
                    submissionDTO,
                    null
            );
        }

        List<FileResponseDTO> feedbackFiles =
                fileRepository
                        .findByUrlIn(
                                feedback.getFileLinks()
                        )
                        .stream()
                        .map(
                                FileResponseDTO::from
                        )
                        .toList();

        ProjectFeedBackWrapperDTO feedbackDTO =
                ProjectFeedBackWrapperDTO.from(
                        feedback,
                        feedbackFiles,
                        feedback.getAdditionalLinks()
                );

        return ProjectInspectionWrapperDTO.from(
                common,
                submissionDTO,
                feedbackDTO
        );
    }

    private Project findAndVerifyProject(String email,
                                         long projectId){
        User user = userFinder.findActiveUserByEmail(email);

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (user instanceof Company c){
            if (!project.getCompany().equals(c)){
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        } else if (user instanceof Crew c){
            if (!project.getSelectedCrew().equals(c)){
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (project.isBeforeSigningContract()) {
            throw new CustomException(
                    ErrorCode.PROJECT_CONTRACT_UNSIGNED
            );
        }

        return project;
    }

    private DetailedProjectResponseDTO createCommon(Project project){
        ProjectSettlement settlement = projectSettlementRepository.findByProject(project);
        return DetailedProjectResponseDTO.create(project, settlement);
    }
}
