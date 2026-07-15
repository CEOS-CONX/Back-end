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
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.response.*;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.repository.ProjectSettlementRepository;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
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
    private final ProjectInspectionFeedbackRepository projectInspectionFeedbackRepository;


    @Transactional(readOnly = true)
    public CompanyWorkspaceDashboardResponse getDashboard(
            Long companyId,
            ProjectStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        CompanyProjectStatusResponseDTO projectStatus = projectRepository.findCompanyStatusWithCompany(company);
        CompanyExpenditureStatusResponseDTO expenditure = projectSettlementRepository.findCompanyStatusWithCompany(company);
        Page<Project> projectPage = projectRepository.findByCompanyWithFilters(
                company, status, startDateTime, endDateTime, pageable
        );

        Page<TodoProjectWrapperDTO> todo = projectPage.map(TodoProjectWrapperDTO::from);


        Map<ProjectStatus, List<TodoProjectWrapperDTO>> grouped = todo.getContent().stream()
                .collect(Collectors.groupingBy(TodoProjectWrapperDTO::projectStatus));

        List<CompanyTodoProjectResponseDTO> todoGroupedByStatus = Arrays.stream(ProjectStatus.values())
                .map(s -> new CompanyTodoProjectResponseDTO(
                        s,
                        grouped.getOrDefault(s, List.of())
                ))
                .toList();

        return CompanyWorkspaceDashboardResponse.of(
                projectStatus, expenditure, todoGroupedByStatus
        );
    }

    @Transactional(readOnly = true)
    public Page<CompanyWorkspaceProjectResponse> getProjects(
            Long companyId,
            String keyword,
            Industry category,
            CrewType crewType,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Company company = userFinder.findActiveCompany(companyId);

        return projectRepository.findCompanyProjectsByFilter(
                        company.getId(),
                        keyword,
                        category,
                        crewType,
                        startDate,
                        endDate,
                        pageable
                )
                .map(CompanyWorkspaceProjectResponse::from);
    }

    @Transactional(readOnly = true)
    public CompanyWorkspaceProjectDetailResponse getProjectDetail(
            Long companyId, Long projectId, int page, int size
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        CompanyProjectDetailResponse common = CompanyProjectDetailResponse.create(project);
        CompanyWorkspaceProjectDetailResponse response;

        if (project.isInProgress()) {
            List<CompanyWorkSpaceForProjectApplicationDTO> applications =
                    projectApplicationRepository.findAllByProject(project).stream().map(
                            CompanyWorkSpaceForProjectApplicationDTO::from
                    ).toList();

            response = ProjectApplicationForCompanyWrapperDTO.from(common, applications);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<InspectionInfoInOneLineDTO> inspections =
                    projectSubmissionRepository.findByProject(project, pageable)
                            .map(InspectionInfoInOneLineDTO::create);

            response = ProjectStatusResponseDTO.create(common, inspections);
        }

        return response;
    }

    @Transactional
    public CompanyProjectIdResponse createProject(Long companyId, CompanyProjectRequestDTO request) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = Project.createRecruitingProject(company, request);

        saveFiles(request);

        Project savedProject = projectRepository.save(project);
        return CompanyProjectIdResponse.from(savedProject);
    }

    @Transactional
    public CompanyProjectIdResponse createProjectDraft(Long companyId, CompanyProjectRequestDTO request) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = Project.createDraft(company, request);

        saveFiles(request);

        Project savedDraft = projectRepository.save(project);
        return CompanyProjectIdResponse.from(savedDraft);
    }

    @Transactional
    public CompanyProjectIdResponse updateProject(Long companyId, Long projectId, CompanyProjectRequestDTO request) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        saveUnexistFiles(request, project);

        project.modifyProject(request);
        return CompanyProjectIdResponse.from(project);
    }

    @Transactional
    public CompanyProjectIdResponse updateProjectDraft(Long companyId, Long draftId, CompanyProjectRequestDTO request) {
        Company company = userFinder.findActiveCompany(companyId);
        Project draft = findCompanyDraft(company.getId(), draftId);
        saveUnexistFiles(request, draft);

        draft.modifyDraft(request);

        return CompanyProjectIdResponse.from(draft);
    }

    @Transactional(readOnly = true)
    public CompanyProjectDraftResponse getProjectDraft(Long companyId, Long draftId) {
        Company company = userFinder.findActiveCompany(companyId);
        Project draft = findCompanyDraft(company.getId(), draftId);

        List<String> fileLinks = draft.getFileLinks();
        List<File> files = fileRepository.findAllByUrlIn(fileLinks);
        List<FileResponseDTO> fileResponseDTOS = files.stream().map(
                FileResponseDTO::from
        ).toList();

        return CompanyProjectDraftResponse.from(draft, fileResponseDTOS);
    }

    @Transactional
    public void deleteProject(Long companyId, Long projectId) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        fileRepository.deleteByUrlIn(project.getFileLinks());
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<CompanyProjectApplicationResponse> getProjectApplications(
            Long companyId,
            Long projectId
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        return projectApplicationRepository.findAllByProjectId(project.getId())
                .stream()
                .map(CompanyProjectApplicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyProjectApplicationDetailResponse getProjectApplicationDetail(
            Long companyId,
            Long projectId,
            Long applicationId
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        ProjectApplication application = findProjectApplication(project.getId(), applicationId);

        return CompanyProjectApplicationDetailResponse.from(application);
    }

    @Transactional
    public CompanyProjectApplicationSelectResponse selectProjectApplication(
            Long companyId,
            Long projectId,
            Long applicationId
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        if (project.getStatus() != ProjectStatus.RECRUITING) {
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        ProjectApplication selectedApplication = findProjectApplication(project.getId(), applicationId);

        if (!selectedApplication.isPending()) {
            throw new CustomException(ErrorCode.INVALID_APPLICATION_STATUS);
        }

        project.selectCrew(selectedApplication.getCrew());
        selectedApplication.select();
        rejectOtherApplications(project.getId(), selectedApplication.getId());

        notificationFacadeService.saveNotificationAboutSelectedProject(project);

        return CompanyProjectApplicationSelectResponse.of(project, selectedApplication);
    }

    @Transactional(readOnly = true)
    public CompanyPartnerCrewResponse getPartnerCrew(Long companyId, Long projectId) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        Crew partnerCrew = findPartnerCrew(project);

        return CompanyPartnerCrewResponse.of(project, partnerCrew);
    }

    /*
    @Transactional
    public CompanyProjectRevisionResponse requestProjectRevision(
            Long companyId,
            Long projectId,
            CompanyProjectRevisionRequest request
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        findPartnerCrew(project);

        ProjectSubmission submission = findLatestSubmission(project.getId());

        if (!submission.isEditable()) {
            throw new CustomException(ErrorCode.INVALID_SUBMISSION_STATUS);
        }

        submission.requestRevision(request.revisionReason());
        project.requestRevision();

        return CompanyProjectRevisionResponse.of(project, submission);
    }
     */

    /*
    @Transactional
    public CompanyProjectApprovalResponse approveProject(
            Long companyId,
            Long projectId
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        findPartnerCrew(project);

        ProjectSubmission submission = findLatestSubmission(project.getId());

        if (!submission.isSubmitted()) {
            throw new CustomException(ErrorCode.INVALID_SUBMISSION_STATUS);
        }

        submission.approve();
        project.approveResult();
        createSettlementIfNotExists(project);

        return CompanyProjectApprovalResponse.of(project, submission);
    }
     */

    private void createSettlementIfNotExists(Project project) {
        if (projectSettlementRepository.existsByProjectId(project.getId())) {
            return;
        }

        ProjectSettlement settlement = ProjectSettlement.create(project);
        projectSettlementRepository.save(settlement);
    }

    private Crew findPartnerCrew(Project project) {
        if (project.getSelectedCrew() == null) {
            throw new CustomException(ErrorCode.PARTNER_CREW_NOT_FOUND);
        }

        return project.getSelectedCrew();
    }

    private ProjectSubmission findLatestSubmission(Long projectId) {
        return projectSubmissionRepository.findTopByProjectIdOrderByIdDesc(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));
    }

    private ProjectApplication findProjectApplication(Long projectId, Long applicationId) {
        return projectApplicationRepository.findByIdAndProjectId(applicationId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @Transactional
    protected void rejectOtherApplications(Long projectId, Long selectedApplicationId) {
        List<ProjectApplication> pendingApplications =
                projectApplicationRepository.findAllByProjectIdAndStatus(
                        projectId,
                        ProjectApplicationStatus.PENDING
                );

        pendingApplications.stream()
                .filter(application -> !application.getId().equals(selectedApplicationId))
                .forEach(application -> {
                    application.reject();
                    notificationFacadeService.saveNotificationAboutRejectedProject(application);
                });
    }

    private Project findCompanyProject(Long companyId, Long projectId) {
        return projectRepository.findByIdAndCompanyId(projectId, companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private Project findCompanyDraft(Long companyId, Long draftId) {
        return projectRepository.findByIdAndCompanyIdAndStatus(draftId, companyId, ProjectStatus.DRAFT)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ProjectInspectionWrapperDTO getProjectReviewDetail(Long companyId, Long projectId, Long submissionId) {
        Company company = userFinder.findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        ProjectSubmission projectSubmission = projectSubmissionRepository.findById(submissionId).orElseThrow(
                () -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND)
        );

        if (!projectSubmission.isEditable()){
            throw new CustomException(ErrorCode.INVALID_SUBMISSION_STATUS);
        }

        CompanyProjectDetailResponse common = CompanyProjectDetailResponse.create(project);
        List<FileResponseDTO> filesInSubmission = fileRepository.findByUrlIn(projectSubmission.getFileLinks()).stream().map(FileResponseDTO::from).toList();
        List<AdditionalLinksWrapper> additionalLinksInSubmission = projectSubmission.getAdditionalLinks();
        ProjectSubmissionWrapperDTO submission = ProjectSubmissionWrapperDTO.from(projectSubmission, filesInSubmission, additionalLinksInSubmission);

        ProjectInspectionFeedback feedback = projectInspectionFeedbackRepository.findBySubmission(projectSubmission);

        if (feedback == null){
            return ProjectInspectionWrapperDTO.from(common, submission, null);
        }

        List<FileResponseDTO> filesInFeedback = fileRepository.findByUrlIn(feedback.getFileLinks()).stream().map(FileResponseDTO::from).toList();
        List<AdditionalLinksWrapper> additionalLinksInFeedback = feedback.getAdditionalLinks();
        ProjectFeedBackWrapperDTO feedBackWrapperDTO = ProjectFeedBackWrapperDTO.from(feedback, filesInFeedback, additionalLinksInFeedback);

        return ProjectInspectionWrapperDTO.from(common, submission, feedBackWrapperDTO);
    }

    @Transactional(readOnly = true)
    public List<CompanySettlementResponse> getSettlements(
            Long companyId,
            ProjectSettlementStatus status
    ) {
        Company company = userFinder.findActiveCompany(companyId);

        if (status == null) {
            return projectSettlementRepository.findAllByCompanyIdOrderByIdDesc(company.getId())
                    .stream()
                    .map(CompanySettlementResponse::from)
                    .toList();
        }

        return projectSettlementRepository.findAllByCompanyIdAndStatusOrderByIdDesc(
                        company.getId(),
                        status
                )
                .stream()
                .map(CompanySettlementResponse::from)
                .toList();
    }

    @Transactional
    public CompanySettlementExpectedPaymentDateResponse updateSettlementExpectedPaymentDate(
            Long companyId,
            Long settlementId,
            CompanySettlementExpectedPaymentDateRequest request
    ) {
        Company company = userFinder.findActiveCompany(companyId);
        ProjectSettlement settlement = findCompanySettlement(company.getId(), settlementId);

        settlement.updateExpectedPaymentDate(request.expectedPaymentDate());

        return CompanySettlementExpectedPaymentDateResponse.from(settlement);
    }

    private ProjectSettlement findCompanySettlement(Long companyId, Long settlementId) {
        return projectSettlementRepository.findByIdAndCompanyId(settlementId, companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));
    }

    private void saveFiles (CompanyProjectRequestDTO request){
        List<File> files = request.fileLinks().stream().map(
                f -> {
                    HeadObjectResponse headObjectResponse = fileService.getHeadObject(f.fileLinks());
                    return File.create(f.originalName(), headObjectResponse, f.fileLinks(), f.explanation());
                }
        ).toList();

        fileRepository.saveAll(files);
    }

    private void saveUnexistFiles (CompanyProjectRequestDTO request, Project project){
        if(!project.getFileLinks().equals(request.fileLinks().stream().map(FileRequestDTO::fileLinks).toList())){
            for (FileRequestDTO dto : request.fileLinks()) {
                if (!fileRepository.existsByUrl(dto.fileLinks())){
                    HeadObjectResponse head = fileService.getHeadObject(dto.fileLinks());

                    fileRepository.save(
                            File.create(dto.originalName(), head, dto.fileLinks(), dto.explanation())
                    );
                }
            }
        }
    }
}