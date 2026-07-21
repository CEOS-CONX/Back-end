package com.conx.server.user.service.workspace;

import com.conx.server.domain.file.domain.File;
import com.conx.server.domain.file.dto.FileRequestDTO;
import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.domain.file.repository.FileRepository;
import com.conx.server.domain.file.service.FileService;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.notification.service.notificationFactory.NotificationFacadeService;
import com.conx.server.project.domain.*;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.project.repository.*;
import com.conx.server.project.service.CrewProjectTodoService;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewEvaluation;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.ProjectStatusFilter;
import com.conx.server.user.dto.company.request.CompanyFeedbackRequestDTO;
import com.conx.server.user.dto.company.request.CompanyProjectEvaluationRequest;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.request.CompanySettlementCompleteRequest;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.*;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDetailResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionListItemResponse;
import com.conx.server.user.repository.CrewEvaluationRepository;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyWorkspaceService {

    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final ProjectSettlementRepository projectSettlementRepository;
    private final NotificationFacadeService notificationFacadeService;
    private final UserFinder userFinder;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final ProjectInspectionFeedbackRepository
            projectInspectionFeedbackRepository;
    private final EvaluationRepository evaluationRepository;
    private final CrewProjectTodoService crewProjectTodoService;
    private final ProjectSubmissionCriteriaRepository projectSubmissionCriteriaRepository;
    private final CrewRepository crewRepository;
    private final CrewEvaluationRepository crewEvaluationRepository;

    /**
     * 기업 워크스페이스 대시보드 조회
     */
    @Transactional(readOnly = true)
    public CompanyWorkspaceDashboardResponse getDashboard(
            Long companyId,
            ProjectStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        LocalDateTime startDateTime =
                startDate == null
                        ? null
                        : startDate.atStartOfDay();

        LocalDateTime endDateTime =
                endDate == null
                        ? null
                        : endDate.atTime(
                        23,
                        59,
                        59
                );

        CompanyProjectStatusResponseDTO projectStatus =
                projectRepository
                        .findCompanyStatusWithCompany(
                                company
                        );

        CompanyExpenditureStatusResponseDTO expenditure =
                projectSettlementRepository
                        .findCompanyStatusWithCompany(
                                company,
                                LocalDate.now().getYear()
                        );

        Page<Project> projectPage =
                projectRepository
                        .findByCompanyWithFilters(
                                company,
                                status,
                                startDateTime,
                                endDateTime,
                                pageable
                        );

        Page<TodoProjectWrapperDTO> todoPage =
                projectPage.map(
                        TodoProjectWrapperDTO::from
                );

        Map<ProjectStatus, List<TodoProjectWrapperDTO>> grouped =
                todoPage.getContent()
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        TodoProjectWrapperDTO::projectStatus
                                )
                        );

        List<CompanyTodoProjectResponseDTO> todoGroupedByStatus =
                Arrays.stream(
                                ProjectStatus.values()
                        )
                        .map(
                                projectStatusValue ->
                                        new CompanyTodoProjectResponseDTO(
                                                projectStatusValue,
                                                grouped.getOrDefault(
                                                        projectStatusValue,
                                                        List.of()
                                                )
                                        )
                        )
                        .toList();

        return CompanyWorkspaceDashboardResponse.of(
                projectStatus,
                expenditure,
                todoGroupedByStatus
        );
    }

    /**
     * 기업 프로젝트 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<CompanyWorkspaceProjectResponse> getProjects(
            Long companyId, String keyword, ProjectStatusFilter status, Industry category, CrewType crewType, LocalDate startDate, LocalDate endDate, Pageable pageable
    ) {
        Company company = userFinder.findActiveCompany(companyId);

        List<ProjectStatus> statuses = (status != null) ? status.getStatuses() : null;

        return projectRepository.findCompanyProjectsByFilter(company.getId(), normalizeKeyword(keyword),
                        statuses, category, crewType, startDate, endDate, pageable)
                .map(CompanyWorkspaceProjectResponse::from);
    }

    /**
     * 파트너 크루 조회
     */
    @Transactional(readOnly = true)
    public Page<CompanyPartnerCrewResponse> getPartnerCrew(
            Long companyId, String keyword, ProjectStatusFilter status,
            Industry category, CrewType crewType, LocalDate startDate, LocalDate endDate, Pageable pageable
    ) {
        Company company = userFinder.findActiveCompany(companyId);

        List<ProjectStatus> statuses = (status != null) ? status.getStatuses() : null;

        Page<Project> projects = crewRepository.findPartnerCrewProjectsByFilter(
                company.getId(), normalizeKeyword(keyword), statuses, category, crewType, startDate, endDate, pageable);

        return projects.map(project -> CompanyPartnerCrewResponse.of(
                project,
                project.getSelectedCrew(),
                crewEvaluationRepository.findByCrew(project.getSelectedCrew())
        ));
    }

    /**
     * 기업 프로젝트 상세 조회
     */
    @Transactional(readOnly = true)
    public CompanyWorkspaceProjectDetailResponse getProjectDetail(
            Long companyId,
            Long projectId,
            int page,
            int size
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project project =
                findCompanyProject(
                        company.getId(),
                        projectId
                );

        DetailedProjectResponseDTO common =
                DetailedProjectResponseDTO.create(
                        project
                );

        if (project.isRecruiting()) {
            List<CompanyWorkSpaceForProjectApplicationDTO> applications =
                    projectApplicationRepository
                            .findAllByProject(
                                    project
                            )
                            .stream()
                            .map(
                                    CompanyWorkSpaceForProjectApplicationDTO::from
                            )
                            .toList();

            return ProjectApplicationForCompanyWrapperDTO.from(
                    common,
                    applications
            );
        }

        Pageable pageable =
                PageRequest.of(
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
     * 프로젝트 등록
     */
    @Transactional
    public CompanyProjectIdResponse createProject(
            Long companyId,
            CompanyProjectRequestDTO request,
            boolean isDraft
    ) {
        Company company = userFinder.findActiveCompany(companyId);

        Project project = Project.createRecruitingProject(company, request);

        if (isDraft) {
            Project draft = projectRepository.findByCompanyAndStatus(company, ProjectStatus.DRAFT).orElseThrow(
                    () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
            );

            projectRepository.delete(draft);
        }

        Project savedProject = projectRepository.save(project);
        saveFiles(request);
        return CompanyProjectIdResponse.from(savedProject);
    }

    /**
     * 프로젝트 임시 저장
     */
    @Transactional
    public CompanyProjectIdResponse createProjectDraft(
            Long companyId,
            CompanyProjectRequestDTO request
    ) {
        Company company = userFinder.findActiveCompany(companyId);

        if (projectRepository.existsByCompanyAndStatus(company, ProjectStatus.DRAFT)) {
            throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
        }

        Project draft = Project.createDraft(company, request);
        Project savedDraft = projectRepository.save(draft);
        saveFiles(request);

        return CompanyProjectIdResponse.from(savedDraft);
    }

    /**
     * 이미 등록된 임시저장 프로젝트가 있는지 조사
     */
    @Transactional
    public boolean findDraft(
            Long companyId
    ){
        Company com = userFinder.findActiveCompany(companyId);
        return projectRepository.existsByCompanyAndStatus(com, ProjectStatus.DRAFT);
    }

    /**
     * 프로젝트 수정
     */
    @Transactional
    public CompanyProjectIdResponse updateProject(
            Long companyId,
            Long projectId,
            CompanyProjectRequestDTO request
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project project =
                findCompanyProject(
                        company.getId(),
                        projectId
                );

        if (project.getStatus() == ProjectStatus.DRAFT) {
            throw new CustomException(
                    ErrorCode.INVALID_PROJECT_STATUS
            );
        }

        saveUnregisteredFiles(
                request,
                project
        );

        project.modifyProject(
                request
        );

        return CompanyProjectIdResponse.from(
                project
        );
    }

    /**
     * 프로젝트 기준충족 버큰 누르기
     */
    @Transactional
    public void checkCriteria(long projectId, long companyId, Long criteriaId) {
        Company company = userFinder.findActiveCompany(companyId);

        ProjectSubmissionCriteria criteria = projectSubmissionCriteriaRepository.findByIdAndCompany(projectId, criteriaId, company).orElseThrow(
                () -> new CustomException(ErrorCode.CRITERIA_NOT_FOUND)
        );

        criteria.mark();
        Project project = criteria.getProject();

        boolean completed = project.getResultCriteria()
                .stream()
                .allMatch(ProjectSubmissionCriteria::isDone);

        if (completed) {
            project.completeInspection();
        }
    }

    /**
     * 프로젝트 임시 저장 수정
     */
    @Transactional
    public CompanyProjectIdResponse updateProjectDraft(
            Long companyId,
            //Long draftId,
            CompanyProjectRequestDTO request
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project draft = projectRepository.findByCompanyAndStatus(company, ProjectStatus.DRAFT).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

//        Project draft =
//                findCompanyDraft(
//                        company.getId(),
//                        draftId
//                );

        saveUnregisteredFiles(
                request,
                draft
        );

        draft.modifyDraft(
                request
        );

        return CompanyProjectIdResponse.from(
                draft
        );
    }

    /**
     * 프로젝트 임시 저장 상세 조회
     */
    @Transactional(readOnly = true)
    public CompanyProjectDraftResponse getProjectDraft(
            Long companyId
            //Long draftId
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project draft = projectRepository.findByCompanyAndStatus(company, ProjectStatus.DRAFT).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

//        Project draft =
//                findCompanyDraft(
//                        company.getId(),
//                        draftId
//                );

        List<FileResponseDTO> files =
                fileRepository
                        .findAllByUrlIn(
                                draft.getFileLinks()
                        )
                        .stream()
                        .map(
                                FileResponseDTO::from
                        )
                        .toList();

        return CompanyProjectDraftResponse.from(
                draft,
                files
        );
    }

    /**
     * 프로젝트 삭제
     */
    @Transactional
    public void deleteProject(
            Long companyId,
            Long projectId
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project project =
                findCompanyProject(
                        company.getId(),
                        projectId
                );

        if (
                project.getFileLinks() != null
                        && !project.getFileLinks().isEmpty()
        ) {
            fileRepository.deleteByUrlIn(
                    project.getFileLinks()
            );
        }

        projectRepository.delete(
                project
        );
    }


    /**
     * 프로젝트 파트너 크루 선정
     */
    @Transactional
    public CompanyProjectApplicationSelectResponse selectProjectApplication(
            Long companyId,
            Long projectId,
            Long applicationId
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project project =
                findCompanyProject(
                        company.getId(),
                        projectId
                );

        if (project.getStatus() != ProjectStatus.RECRUITING) {
            throw new CustomException(
                    ErrorCode.INVALID_PROJECT_STATUS
            );
        }

        ProjectApplication selectedApplication =
                findProjectApplication(
                        project.getId(),
                        applicationId
                );

        if (!selectedApplication.isPending()) {
            throw new CustomException(
                    ErrorCode.INVALID_APPLICATION_STATUS
            );
        }

        project.selectCrew(
                selectedApplication.getCrew()
        );

        selectedApplication.select();

        rejectOtherApplications(
                project.getId(),
                selectedApplication.getId()
        );

        createSettlementIfNotExists(
                project
        );

        notificationFacadeService
                .saveNotificationAboutSelectedProject(
                        project
                );

        return CompanyProjectApplicationSelectResponse.of(
                project,
                selectedApplication
        );
    }

    /**
     * 결과물 및 피드백 상세 조회
     */
    @Transactional(readOnly = true)
    public ProjectInspectionWrapperDTO getProjectReviewDetail(
            Long companyId,
            Long projectId,
            Long submissionId
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project project =
                findCompanyProject(
                        company.getId(),
                        projectId
                );

        ProjectSubmission submission =
                findVisibleSubmission(
                        project.getId(),
                        submissionId
                );

        DetailedProjectResponseDTO common =
                DetailedProjectResponseDTO.create(
                        project
                );

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

    /**
     * 결과물 피드백 등록
     *
     * 최신 기획에서 수정 요청·승인 기능은 제거되었으므로
     * 결과물에는 피드백만 등록한다.
     */
    @Transactional
    public ProjectInspectionWrapperDTO registerFeedBack(
            Long companyId,
            Long submissionId,
            CompanyFeedbackRequestDTO request
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        ProjectSubmission submission =
                projectSubmissionRepository
                        .findById(
                                submissionId
                        )
                        .orElseThrow(
                                () -> new CustomException(
                                        ErrorCode.SUBMISSION_NOT_FOUND
                                )
                        );

        if (!submission.getProject().getCompany().equals(company)
        ) {
            throw new CustomException(
                    ErrorCode.FORBIDDEN
            );
        }

        if (!submission.canRegisterFeedback()) {
            throw new CustomException(ErrorCode.INVALID_SUBMISSION_STATUS);
        }

        ProjectInspectionFeedback feedback = ProjectInspectionFeedback.create(submission, request);
        saveFiles(request);

        projectInspectionFeedbackRepository.save(feedback);

        submission.setFeedback();

        Project project = submission.getProject();
        project.completeInspection();

        crewProjectTodoService.createIfAbsent(
                findPartnerCrew(project),
                project,
                CrewProjectTodoType.SETTLEMENT_CONFIRMATION
        );

        DetailedProjectResponseDTO common =
                DetailedProjectResponseDTO.create(
                        project
                );

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

    /**
     * 크루 평가 등록
     */
    @Transactional
    public CompanyProjectEvaluationResponse evaluateProject(
            Long companyId,
            Long projectId,
            CompanyProjectEvaluationRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Project project =
                findCompanyProject(
                        company.getId(),
                        projectId
                );

        if (
                project.getStatus() != ProjectStatus.ADJUSTING
                        && project.getStatus() != ProjectStatus.DONE
        ) {
            throw new CustomException(
                    ErrorCode.PROJECT_EVALUATION_NOT_ALLOWED
            );
        }

        Crew selectedCrew =
                findPartnerCrew(
                        project
                );

        if (
                evaluationRepository.existsByProjectId(
                        project.getId()
                )
        ) {
            throw new CustomException(
                    ErrorCode.PROJECT_EVALUATION_ALREADY_EXISTS
            );
        }

        Evaluation evaluation =
                Evaluation.create(
                        project,
                        selectedCrew,
                        company,
                        request.completeness(),
                        request.schedule(),
                        request.ability(),
                        request.reCooperation(),
                        request.communication()
                );

        Evaluation savedEvaluation =
                evaluationRepository.save(
                        evaluation
                );

        CrewEvaluation crewEvaluation = crewEvaluationRepository.findByCrew(selectedCrew);
        crewEvaluation.addEvaluation(evaluation);

        return CompanyProjectEvaluationResponse.from(
                savedEvaluation
        );
    }

    /**
     * 기존 기업 정산 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CompanySettlementResponse> getSettlements(
            Long companyId,
            ProjectSettlementStatus status
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        List<ProjectSettlement> settlements =
                status == null
                        ? projectSettlementRepository
                        .findAllByCompanyIdOrderByIdDesc(
                                company.getId()
                        )
                        : projectSettlementRepository
                        .findAllByCompanyIdAndStatusOrderByIdDesc(
                                company.getId(),
                                status
                        );

        return settlements
                .stream()
                .map(
                        CompanySettlementResponse::from
                )
                .toList();
    }

    /**
     * 정산 예정일 수정
     */
    @Transactional
    public CompanySettlementExpectedPaymentDateResponse
    updateSettlementExpectedPaymentDate(
            Long companyId,
            Long settlementId,
            CompanySettlementExpectedPaymentDateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        ProjectSettlement settlement =
                findCompanySettlement(
                        company.getId(),
                        settlementId
                );

        settlement.updateExpectedPaymentDate(
                request.expectedPaymentDate()
        );

        return CompanySettlementExpectedPaymentDateResponse.from(
                settlement
        );
    }

    /**
     * 기업 정산 관리 현황 조회
     */
    @Transactional(readOnly = true)
    public SubsidyStatusResponse getCompanySubsidyStatus(
            Long companyId,
            ProjectSettlementStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        Page<ProjectSettlement> settlements =
                projectSettlementRepository
                        .findByCompanyAndFilters(
                                company,
                                status,
                                startDate,
                                endDate,
                                pageable
                        );

        SubsidyStatusWrapperDTO subsidyStatus =
                SubsidyStatusWrapperDTO.from(
                        company,
                        settlements
                );

        List<AdjustmentWrapperDTO> adjustmentStatuses =
                settlements
                        .stream()
                        .map(
                                settlement ->
                                        AdjustmentWrapperDTO.from(
                                                settlement.getProject(),
                                                settlement
                                        )
                        )
                        .toList();

        return new SubsidyStatusResponse(
                subsidyStatus,
                adjustmentStatuses
        );
    }

    /**
     * 정산 완료 처리
     */
    @Transactional
    public CompanySettlementCompleteResponse completeSettlement(
            Long companyId,
            Long settlementId,
            CompanySettlementCompleteRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(
                        companyId
                );

        ProjectSettlement settlement =
                findCompanySettlementForUpdate(
                        company.getId(),
                        settlementId
                );

        if (settlement.getStatus() == ProjectSettlementStatus.PAID) {
            throw new CustomException(
                    ErrorCode.SETTLEMENT_ALREADY_PAID
            );
        }

        Project project =
                settlement.getProject();

        if (project.getStatus() != ProjectStatus.ADJUSTING) {
            throw new CustomException(
                    ErrorCode.INVALID_PROJECT_STATUS
            );
        }

        settlement.markAsPaid(
                request.settlementDate()
        );

        project.end();

        crewProjectTodoService.completeIfExists(
                settlement.getCrew(),
                project,
                CrewProjectTodoType.SETTLEMENT_CONFIRMATION
        );

        return CompanySettlementCompleteResponse.from(
                settlement
        );
    }

    private void createSettlementIfNotExists(
            Project project
    ) {
        if (
                projectSettlementRepository.existsByProjectId(
                        project.getId()
                )
        ) {
            return;
        }

        projectSettlementRepository.save(
                ProjectSettlement.create(
                        project
                )
        );
    }

    private Crew findPartnerCrew(
            Project project
    ) {
        if (project.getSelectedCrew() == null) {
            throw new CustomException(
                    ErrorCode.PARTNER_CREW_NOT_FOUND
            );
        }

        return project.getSelectedCrew();
    }

    private ProjectSubmission findVisibleSubmission(
            Long projectId,
            Long submissionId
    ) {
        ProjectSubmission submission =
                projectSubmissionRepository
                        .findByIdAndProjectId(
                                submissionId,
                                projectId
                        )
                        .orElseThrow(
                                () -> new CustomException(
                                        ErrorCode.SUBMISSION_NOT_FOUND
                                )
                        );

        if (submission.getStatus() == ProjectSubmissionStatus.DRAFT) {
            throw new CustomException(
                    ErrorCode.SUBMISSION_NOT_FOUND
            );
        }

        return submission;
    }

    private ProjectApplication findProjectApplication(
            Long projectId,
            Long applicationId
    ) {
        return projectApplicationRepository
                .findByIdAndProjectId(
                        applicationId,
                        projectId
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.APPLICATION_NOT_FOUND
                        )
                );
    }

    @Transactional
    protected void rejectOtherApplications(
            Long projectId,
            Long selectedApplicationId
    ) {
        List<ProjectApplication> pendingApplications =
                projectApplicationRepository
                        .findAllByProjectIdAndStatus(
                                projectId,
                                ProjectApplicationStatus.PENDING
                        );

        pendingApplications
                .stream()
                .filter(
                        application ->
                                !application
                                        .getId()
                                        .equals(
                                                selectedApplicationId
                                        )
                )
                .forEach(
                        application -> {
                            application.reject();

                            notificationFacadeService
                                    .saveNotificationAboutRejectedProject(
                                            application
                                    );
                        }
                );
    }

    private Project findCompanyProject(
            Long companyId,
            Long projectId
    ) {
        return projectRepository
                .findByIdAndCompanyId(
                        projectId,
                        companyId
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.PROJECT_NOT_FOUND
                        )
                );
    }

    private Project findCompanyDraft(
            Long companyId,
            Long draftId
    ) {
        return projectRepository
                .findByIdAndCompanyIdAndStatus(
                        draftId,
                        companyId,
                        ProjectStatus.DRAFT
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.PROJECT_NOT_FOUND
                        )
                );
    }

    private ProjectSettlement findCompanySettlement(
            Long companyId,
            Long settlementId
    ) {
        return projectSettlementRepository
                .findByIdAndCompanyId(
                        settlementId,
                        companyId
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.SETTLEMENT_NOT_FOUND
                        )
                );
    }

    private ProjectSettlement findCompanySettlementForUpdate(
            Long companyId,
            Long settlementId
    ) {
        return projectSettlementRepository
                .findByIdAndCompanyIdForUpdate(
                        settlementId,
                        companyId
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.SETTLEMENT_NOT_FOUND
                        )
                );
    }

    private void saveFiles(
            CompanyProjectRequestDTO request
    ) {
        List<FileRequestDTO> fileRequests =
                request.fileLinks() == null
                        ? List.of()
                        : request.fileLinks();

        List<File> files = fileRequests.stream().map(
                fileRequest -> { HeadObjectResponse head = fileService.getHeadObject(
                                                    fileRequest.fileLinks());
                                return File.create(fileRequest.originalName(), head, fileRequest.fileLinks(), fileRequest.explanation());
                                }
                        ).toList();

        if (!files.isEmpty()) {
            fileRepository.saveAll(
                    files
            );
        }
    }

    private void saveFiles(
            CompanyFeedbackRequestDTO request
    ) {
        List<FileRequestDTO> fileRequests =
                request.files() == null
                        ? List.of()
                        : request.files();

        List<File> files =
                fileRequests
                        .stream()
                        .map(
                                fileRequest -> {
                                    HeadObjectResponse head =
                                            fileService.getHeadObject(
                                                    fileRequest.fileLinks()
                                            );

                                    return File.create(
                                            fileRequest.originalName(),
                                            head,
                                            fileRequest.fileLinks(),
                                            fileRequest.explanation()
                                    );
                                }
                        )
                        .toList();

        if (!files.isEmpty()) {
            fileRepository.saveAll(
                    files
            );
        }
    }

    private void saveUnregisteredFiles(
            CompanyProjectRequestDTO request,
            Project project
    ) {
        List<FileRequestDTO> fileRequests =
                request.fileLinks() == null
                        ? List.of()
                        : request.fileLinks();

        List<String> requestedUrls =
                fileRequests
                        .stream()
                        .map(
                                FileRequestDTO::fileLinks
                        )
                        .toList();

        if (project.getFileLinks().equals(requestedUrls)) {
            return;
        }

        for (FileRequestDTO fileRequest : fileRequests) {
            if (
                    fileRepository.existsByUrl(
                            fileRequest.fileLinks()
                    )
            ) {
                continue;
            }

            HeadObjectResponse head =
                    fileService.getHeadObject(
                            fileRequest.fileLinks()
                    );

            fileRepository.save(
                    File.create(
                            fileRequest.originalName(),
                            head,
                            fileRequest.fileLinks(),
                            fileRequest.explanation()
                    )
            );
        }
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
