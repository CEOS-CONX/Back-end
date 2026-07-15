package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.service.notificationFactory.NotificationFacadeService;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;
import com.conx.server.project.dto.response.DetailedProjectWrapperForCrewWorkSpaceDTO;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.repository.ProjectSettlementRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewWorkspaceProjectStatus;
import com.conx.server.user.dto.crew.CrewWorkspaceSort;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.*;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.service.common.UserFinder;
import com.conx.server.project.service.CrewProjectTodoService;
import com.conx.server.project.domain.CrewProjectTodo;
import com.conx.server.project.repository.CrewProjectTodoRepository;
import com.conx.server.user.dto.crew.CrewTodoProgressStatus;
import com.conx.server.project.domain.enums.CrewProjectTodoStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

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

    private final CrewProjectTodoService
            crewProjectTodoService;

    private final CrewProjectTodoRepository
            crewProjectTodoRepository;

    /**
     * 크루 대시보드 가져오기
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
                evaluationRepository.getEvaluationByCrew(crew);

        CrewProjectInfoDTO crewProjectInfo =
                getProjectDashboard(crew);

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
     * 프로젝트 지원현황 가져오기
     */
    public CrewApplicationStatusResponseDTO getCrewApplicationStatus(
            ApplicationBrowseFilter browseFilter,
            CustomUserDetails customUserDetails
    ){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

        List<ProjectApplicationWrapperDTO> crewApplicationStatus;
        if (browseFilter.equals(ApplicationBrowseFilter.ALL)){
            crewApplicationStatus = projectApplicationRepository
                    .findProjectApplicationByCrew(crew);
        } else {
            crewApplicationStatus = projectApplicationRepository
                    .findProjectApplicationByCrewAndStatus(crew, browseFilter.toApplicationStatus());
        }

        return new CrewApplicationStatusResponseDTO(crewApplicationStatus);
    }

    /**
     * 크루 프로젝트 현황 조회
     */
    @Transactional(readOnly = true)
    public Page<CrewProjectStatusItemResponse>
    getCrewProjects(
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
                normalizeKeyword(keyword);

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
    public Page<CrewTodoProjectResponse>
    getCrewTodoProjects(
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
                normalizeKeyword(keyword);

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
    public CrewSettlementSummaryResponse
    getCrewSettlementSummary(
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

        LocalDate monthStart =
                currentMonth.atDay(1);

        LocalDate monthEnd =
                currentMonth.atEndOfMonth();

        return projectSettlementRepository
                .findCrewSettlementSummary(
                        crew.getId(),
                        monthStart,
                        monthEnd
                );
    }

    /**
     * 크루 정산 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<CrewSettlementItemResponse>
    getCrewSettlements(
            CustomUserDetails customUserDetails,
            String keyword,
            ProjectSettlementStatus settlementStatus,
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
                normalizeKeyword(keyword);

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
                        category,
                        projectType,
                        startDate,
                        endDate,
                        resolvedSort.name(),
                        pageable
                )
                .map(
                        CrewSettlementItemResponse::from
                );
    }

    /**
     * 프로젝트 워크스페이스 가져오기
     */
    @Transactional(readOnly = true)
    public CrewWorkSpaceResponseDTO getCrewWorkSpace (CustomUserDetails customUserDetails){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

        List<Project> projects = projectRepository.findAllBySelectedCrew(crew);
        List<ProjectWrapperForCrewWorkSpaceDTO> projectDTOS = projects.stream().map(
                ProjectWrapperForCrewWorkSpaceDTO::create
        ).toList();

        return new CrewWorkSpaceResponseDTO(projectDTOS);
    }

    /**
     * 프로젝트 상세 워크플레이스 가져오기
     * 이미 저장한 값이 있다면 그 정보를 가져옵니다.
     */
    @Transactional(readOnly = true)
    public CrewProjectWorkSpaceDTO getDetailedCrewWorkSpace(CustomUserDetails customUserDetails, long projectId){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (project.isDone()){
            throw new CustomException(ErrorCode.PROJECT_ALREADY_END);
        }

        if (project.isBeforeSigningContract()){
            throw new CustomException(ErrorCode.PROJECT_CONTRACT_UNSIGNED);
        }

        ProjectSubmission submission = projectSubmissionRepository.findByProject(project)
                .orElse(
                        null
                );
        CrewProjectSubmissionDTO submissionDTO = CrewProjectSubmissionDTO.create(submission);
        DetailedProjectWrapperForCrewWorkSpaceDTO projectWrapper = DetailedProjectWrapperForCrewWorkSpaceDTO.create(project);
        ProjectSubmitConditionDTO submitCondition = ProjectSubmitConditionDTO.create(project);

        if (submission == null){
            return new CrewProjectWorkSpaceDTO(false, projectWrapper, null, submitCondition);
        } else {
            return new CrewProjectWorkSpaceDTO(submission.isEditable(), projectWrapper, submissionDTO, submitCondition);
        }
    }

    /**
     * 결과물 제출하기
     */
    @Transactional
    public void submitProjectResult(CustomUserDetails customUserDetails, long projectId,
                                    SubmitProjectResultRequestDTO req){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());
        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (!project.isWaitingResult()){
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        if (project.isBeforeSigningContract()){
            throw new CustomException(ErrorCode.PROJECT_CONTRACT_UNSIGNED);
        }

        Optional<ProjectSubmission> submissionOptional = projectSubmissionRepository.findByProject(
                project
        );

        if (submissionOptional.isPresent()){
            ProjectSubmission submission = submissionOptional.get();

            if (!submission.isEditable()){
                throw new CustomException(ErrorCode.INVALID_SUBMISSION_STATUS);
            }

            submission.update(req);
            submission.activateSubmission();
        } else {
            ProjectSubmission submission = ProjectSubmission.create(project, req.content(), req.fileLinks());
            projectSubmissionRepository.save(submission);
        }

        project.submitProjectResult();

        crewProjectTodoService.completeSubmissionTodo(
                crew,
                project
        );

        notificationFacadeService
                .saveNotificationAboutResultUploaded(project);
    }

    /**
     * 결과물 임시 저장하기
     */
    @Transactional
    public void draftProjectResult(CustomUserDetails customUserDetails, long projectId,
                                   SubmitProjectResultRequestDTO req){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());
        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (project.getStatus() != ProjectStatus.WAITING_RESULT){
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        Optional<ProjectSubmission> submissionOptional = projectSubmissionRepository.findByProject(
                project
        );

        if (submissionOptional.isPresent()){
            submissionOptional.get().update(req);
        } else {
            ProjectSubmission submission = ProjectSubmission.createDraft(project, req.content(), req.fileLinks());
            projectSubmissionRepository.save(submission);
        }
    }

    private String normalizeKeyword(
            String keyword
    ) {
        if (
                keyword == null
                        || keyword.isBlank()
        ) {
            return null;
        }

        return keyword.trim();
    }
}