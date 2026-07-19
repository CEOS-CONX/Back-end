package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.service.notificationFactory.NotificationFacadeService;
import com.conx.server.project.domain.CrewProjectTodo;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
import com.conx.server.project.domain.enums.CrewProjectTodoStatus;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.project.dto.response.DetailedProjectWrapperForCrewWorkSpaceDTO;
import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;
import com.conx.server.project.repository.CrewProjectTodoRepository;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.repository.ProjectSettlementRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.project.service.CrewProjectTodoService;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.DetailedProjectResponseDTO;
import com.conx.server.user.dto.company.response.InspectionInfoInOneLineDTO;
import com.conx.server.user.dto.company.response.ProjectStatusResponseDTO;
import com.conx.server.user.dto.crew.CrewTodoProgressStatus;
import com.conx.server.user.dto.crew.CrewWorkspaceProjectStatus;
import com.conx.server.user.dto.crew.CrewWorkspaceSort;
import com.conx.server.user.dto.crew.request.CrewPaymentStatusUpdateRequest;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.dto.crew.response.CrewDashboardResultDTO;
import com.conx.server.user.dto.crew.response.CrewEvaluationWrapperDTO;
import com.conx.server.user.dto.crew.response.CrewPaymentStatusUpdateResponse;
import com.conx.server.user.dto.crew.response.CrewProjectInfoDTO;
import com.conx.server.user.dto.crew.response.CrewProjectStatusItemResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDTO;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDetailResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionListItemResponse;
import com.conx.server.user.dto.crew.response.CrewProjectWorkSpaceDTO;
import com.conx.server.user.dto.crew.response.CrewProjectWorkspaceDetailResponse;
import com.conx.server.user.dto.crew.response.CrewSettlementItemResponse;
import com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse;
import com.conx.server.user.dto.crew.response.CrewTodoProjectResponse;
import com.conx.server.user.dto.crew.response.CrewWorkSpaceResponseDTO;
import com.conx.server.user.dto.crew.response.ProjectSubmitConditionDTO;
import com.conx.server.user.dto.crew.response.ProjectWrapperForCrewWorkSpaceDTO;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewWorkSpaceService {

    private static final int DASHBOARD_TODO_LIMIT = 3;

    private final ProjectRepository projectRepository;
    private final UserFinder userFinder;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final NotificationFacadeService notificationFacadeService;
    private final EvaluationRepository evaluationRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectSettlementRepository projectSettlementRepository;
    private final CrewProjectTodoService crewProjectTodoService;
    private final CrewProjectTodoRepository crewProjectTodoRepository;

    private CrewProjectInfoDTO getProjectDashboard(
            Crew crew
    ) {
        long appliedProjectAmount =
                projectApplicationRepository.countByCrewAndStatus(
                        crew,
                        ProjectApplicationStatus.PENDING
                );

        long progressProjectAmount =
                projectRepository.countBySelectedCrewAndStatusIn(
                        crew,
                        List.of(
                                ProjectStatus.CONTRACT_PENDING,
                                ProjectStatus.PROGRESS
                        )
                );

        long executionCompletedProjectAmount =
                projectRepository.countBySelectedCrewAndStatus(
                        crew,
                        ProjectStatus.WAITING_RESULT
                );

        long submissionCompletedProjectAmount =
                projectRepository.countBySelectedCrewAndStatusIn(
                        crew,
                        List.of(
                                ProjectStatus.INSPECTION,
                                ProjectStatus.ADJUSTING
                        )
                );

        long settlementCompletedProjectAmount =
                projectSettlementRepository.countByCrewAndStatus(
                        crew,
                        ProjectSettlementStatus.PAID
                );

        return new CrewProjectInfoDTO(
                appliedProjectAmount,
                progressProjectAmount,
                executionCompletedProjectAmount,
                submissionCompletedProjectAmount,
                settlementCompletedProjectAmount
        );
    }

    /**
     * 크루 대시보드 조회
     */
    @Transactional(readOnly = true)
    public CrewDashboardResultDTO getCrewDashboard(
            CustomUserDetails customUserDetails
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        long totalSubsidy =
                projectSettlementRepository.sumAmountByCrewAndStatus(
                        crew,
                        ProjectSettlementStatus.PAID
                );

        CrewEvaluationWrapperDTO evaluation =
                evaluationRepository.getEvaluationByCrew(
                        crew
                );

        CrewProjectInfoDTO crewProjectInfo =
                getProjectDashboard(
                        crew
                );

        List<CrewTodoProjectResponse> todoProjects =
                crewProjectTodoRepository
                        .findDashboardTodos(
                                crew.getId(),
                                CrewProjectTodoStatus.COMPLETED,
                                PageRequest.of(
                                        0,
                                        DASHBOARD_TODO_LIMIT
                                )
                        )
                        .stream()
                        .map(
                                CrewTodoProjectResponse::from
                        )
                        .toList();

        return new CrewDashboardResultDTO(
                totalSubsidy,
                evaluation,
                crewProjectInfo,
                todoProjects
        );
    }

    /**
     * 프로젝트 지원 현황 조회
     */
    @Transactional(readOnly = true)
    public CrewApplicationStatusResponseDTO getCrewApplicationStatus(
            ApplicationBrowseFilter browseFilter,
            CustomUserDetails customUserDetails
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        List<ProjectApplicationWrapperDTO> crewApplicationStatus;

        if (browseFilter == ApplicationBrowseFilter.ALL) {
            crewApplicationStatus =
                    projectApplicationRepository
                            .findProjectApplicationByCrew(
                                    crew
                            );
        } else {
            crewApplicationStatus =
                    projectApplicationRepository
                            .findProjectApplicationByCrewAndStatus(
                                    crew,
                                    browseFilter.toApplicationStatus()
                            );
        }

        return new CrewApplicationStatusResponseDTO(
                crewApplicationStatus
        );
    }

    /**
     * 크루 프로젝트 현황 조회
     */
    @Transactional(readOnly = true)
    public Page<CrewProjectStatusItemResponse> getCrewProjects(
            CustomUserDetails customUserDetails,
            String keyword,
            CrewWorkspaceProjectStatus status,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            CrewWorkspaceSort sort,
            int page,
            int size
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        String normalizedKeyword =
                normalizeKeyword(
                        keyword
                );

        String workspaceStatus =
                status == null
                        ? null
                        : status.name();

        CrewWorkspaceSort resolvedSort =
                sort == null
                        ? CrewWorkspaceSort.RECENT
                        : sort;

        Sort registeredAtSort =
                resolvedSort == CrewWorkspaceSort.OLDEST
                        ? Sort.by(
                        Sort.Order.asc("createdAt"),
                        Sort.Order.asc("id")
                )
                        : Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                );

        Pageable pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.max(size, 1),
                        registeredAtSort
                );

        return projectApplicationRepository
                .findCrewWorkspaceProjects(
                        crew.getId(),
                        normalizedKeyword,
                        workspaceStatus,
                        category,
                        projectType,
                        startDate,
                        endDate,
                        pageable
                )
                .map(
                        CrewProjectStatusItemResponse::from
                );
    }

    /**
     * 크루 Todo 프로젝트 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<CrewTodoProjectResponse> getCrewTodoProjects(
            CustomUserDetails customUserDetails,
            String keyword,
            CrewTodoProgressStatus progressStatus,
            CrewWorkspaceSort sort,
            int page,
            int size
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        String normalizedKeyword =
                normalizeKeyword(
                        keyword
                );

        CrewWorkspaceSort resolvedSort =
                sort == null
                        ? CrewWorkspaceSort.RECENT
                        : sort;

        Sort registeredAtSort =
                resolvedSort == CrewWorkspaceSort.OLDEST
                        ? Sort.by(
                        Sort.Order.asc("createdAt"),
                        Sort.Order.asc("id")
                )
                        : Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                );

        Pageable pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.max(size, 1),
                        registeredAtSort
                );

        Page<CrewProjectTodo> todoPage;

        if (progressStatus == null) {
            todoPage =
                    crewProjectTodoRepository
                            .findCrewTodoProjects(
                                    crew.getId(),
                                    normalizedKeyword,
                                    pageable
                            );
        } else {
            todoPage =
                    crewProjectTodoRepository
                            .findCrewTodoProjectsByStatus(
                                    crew.getId(),
                                    normalizedKeyword,
                                    progressStatus.toDomainStatus(),
                                    pageable
                            );
        }

        return todoPage.map(
                CrewTodoProjectResponse::from
        );
    }

    /**
     * 크루 정산 요약 조회
     */
    @Transactional(readOnly = true)
    public CrewSettlementSummaryResponse getCrewSettlementSummary(
            CustomUserDetails customUserDetails
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        YearMonth currentMonth =
                YearMonth.now(
                        ZoneId.of("Asia/Seoul")
                );

        CrewSettlementSummaryResponse summary =
                projectSettlementRepository
                        .findCrewSettlementSummary(
                                crew.getId(),
                                currentMonth.atDay(1),
                                currentMonth.atEndOfMonth()
                        );

        return summary == null
                ? new CrewSettlementSummaryResponse(0, 0, 0, null)
                : summary;
    }

    /**
     * 크루 정산 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<CrewSettlementItemResponse> getCrewSettlements(
            CustomUserDetails customUserDetails,
            String keyword,
            ProjectSettlementStatus settlementStatus,
            CrewPaymentStatus crewPaymentStatus,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate settlementStartDate,
            LocalDate settlementEndDate,
            CrewWorkspaceSort sort,
            int page,
            int size
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        String normalizedKeyword =
                normalizeKeyword(
                        keyword
                );

        CrewWorkspaceSort resolvedSort =
                sort == null
                        ? CrewWorkspaceSort.RECENT
                        : sort;

        Pageable pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.max(size, 1)
                );

        return projectSettlementRepository
                .findCrewSettlements(
                        crew.getId(),
                        normalizedKeyword,
                        settlementStatus,
                        crewPaymentStatus,
                        category,
                        projectType,
                        startDate,
                        endDate,
                        settlementStartDate,
                        settlementEndDate,
                        resolvedSort.name(),
                        pageable
                )
                .map(
                        CrewSettlementItemResponse::from
                );
    }

    /**
     * 크루 지급 확인 상태 변경
     */
    @Transactional
    public CrewPaymentStatusUpdateResponse updateCrewPaymentStatus(
            CustomUserDetails customUserDetails,
            Long settlementId,
            CrewPaymentStatusUpdateRequest request
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        ProjectSettlement settlement =
                projectSettlementRepository
                        .findByIdAndCrewIdForUpdate(
                                settlementId,
                                crew.getId()
                        )
                        .orElseThrow(
                                () -> new CustomException(
                                        ErrorCode.SETTLEMENT_NOT_FOUND
                                )
                        );

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        settlement.changeCrewPaymentStatus(
                request.paymentStatus(),
                today
        );

        return CrewPaymentStatusUpdateResponse.from(
                settlement
        );
    }

    /**
     * 기존 크루 프로젝트 워크스페이스 목록 조회
     */
    @Transactional(readOnly = true)
    public CrewWorkSpaceResponseDTO getCrewWorkSpace(
            CustomUserDetails customUserDetails
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        List<ProjectWrapperForCrewWorkSpaceDTO> projectDTOs =
                projectRepository
                        .findAllBySelectedCrew(
                                crew
                        )
                        .stream()
                        .map(
                                ProjectWrapperForCrewWorkSpaceDTO::create
                        )
                        .toList();

        return new CrewWorkSpaceResponseDTO(
                projectDTOs
        );
    }

    /**
     * 기존 프로젝트 상세 워크스페이스 조회
     */
    @Transactional(readOnly = true)
    public ProjectStatusResponseDTO getProjectDetail(
            Long crewId,
            Long projectId,
            int page,
            int size
    ) {
        Crew crew = userFinder.findActiveCrew(crewId);

        Project project =
                findCrewWorkspaceProject(
                        crew,
                        projectId
                );

        if (project.isDone()) {
            throw new CustomException(
                    ErrorCode.PROJECT_ALREADY_END
            );
        }

        if (project.isBeforeSigningContract()) {
            throw new CustomException(
                    ErrorCode.PROJECT_CONTRACT_UNSIGNED
            );
        }

        DetailedProjectResponseDTO common = DetailedProjectResponseDTO.create(project);
        Pageable pageable = PageRequest.of(
                        Math.max(page, 0),
                        Math.max(size, 1),
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        Page<InspectionInfoInOneLineDTO> submissions =
                projectSubmissionRepository
                        .findAllByProjectIdAndStatusNotOrderByIdDesc(
                                project.getId(),
                                ProjectSubmissionStatus.DRAFT,
                                pageable
                        )
                        .map(
                                InspectionInfoInOneLineDTO::create
                        );

        return ProjectStatusResponseDTO.create(
                common,
                submissions
        );
    }

    /**
     * 결과물 제출
     */
    @Transactional
    public void submitProjectResult(
            CustomUserDetails customUserDetails,
            long projectId,
            SubmitProjectResultRequestDTO request
    ) {
        Crew crew =
                userFinder.findActiveCrew(
                        customUserDetails.getId()
                );

        Project project =
                findCrewWorkspaceProject(
                        crew,
                        projectId
                );

        if (!project.isWaitingResult()) {
            throw new CustomException(
                    ErrorCode.INVALID_PROJECT_STATUS
            );
        }

        if (project.isBeforeSigningContract()) {
            throw new CustomException(
                    ErrorCode.PROJECT_CONTRACT_UNSIGNED
            );
        }

        ProjectSubmission submission = ProjectSubmission.create(project, crew, request.subject(),
                request.content(), request.fileLinks(), request.links());

        project.submitProjectResult();
        projectSubmissionRepository.save(submission);
        crewProjectTodoService.completeSubmissionTodo(crew, project);
        notificationFacadeService.saveNotificationAboutResultUploaded(project);
    }


    /**
     * 선택된 크루 본인의 프로젝트인지 검증
     */
    private Project findCrewWorkspaceProject(
            Crew crew,
            long projectId
    ) {
        return projectRepository
                .findBySelectedCrewAndId(
                        crew,
                        projectId
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.PROJECT_NOT_FOUND
                        )
                );
    }

    private String normalizeKeyword(
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}
