package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.company.request.CompanyProjectRequest;
import com.conx.server.user.dto.company.response.CompanyProjectDraftResponse;
import com.conx.server.user.dto.company.response.CompanyProjectIdResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceDashboardResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectResponse;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationDetailResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationSelectResponse;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.user.dto.company.response.CompanyPartnerCrewResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApprovalResponse;
import com.conx.server.user.dto.company.response.CompanyProjectRevisionResponse;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.repository.ProjectSettlementRepository;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.CompanySettlementExpectedPaymentDateResponse;
import com.conx.server.user.dto.company.response.CompanySettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompanyWorkspaceService {

    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final ProjectSettlementRepository projectSettlementRepository;

    @Transactional(readOnly = true)
    public CompanyWorkspaceDashboardResponse getDashboard(Long companyId) {
        Company company = findActiveCompany(companyId);

        long totalProjectCount = projectRepository.countByCompanyIdAndStatusNot(
                company.getId(),
                ProjectStatus.DRAFT
        );

        long recruitingProjectCount = projectRepository.countByCompanyIdAndStatus(
                company.getId(),
                ProjectStatus.RECRUITING
        );

        return CompanyWorkspaceDashboardResponse.of(
                totalProjectCount,
                recruitingProjectCount
        );
    }

    @Transactional(readOnly = true)
    public List<CompanyWorkspaceProjectResponse> getProjects(
            Long companyId,
            String keyword,
            Industry category,
            ProjectType projectType,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Company company = findActiveCompany(companyId);

        return projectRepository.findCompanyProjectsByFilter(
                        company.getId(),
                        keyword,
                        projectType,
                        startDate,
                        endDate
                )
                .stream()
                .map(CompanyWorkspaceProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyWorkspaceProjectDetailResponse getProjectDetail(Long companyId, Long projectId) {
        Company company = findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        return CompanyWorkspaceProjectDetailResponse.from(project);
    }

    @Transactional
    public CompanyProjectIdResponse createProject(Long companyId, CompanyProjectRequest request) {
        Company company = findActiveCompany(companyId);

        Project project = Project.createRecruitingProject(
                company,
                request.projectImage(),
                request.brandName(),
                request.managerName(),
                request.managerEmail(),
                request.managerPhone(),
                request.name(),
                request.objectives(),
                request.projectType(),
                request.requirement(),
                request.projectExplanation(),
                request.resultForm(),
                request.essentialSubmitPart(),
                request.recruitDeadLine(),
                request.projectStartDate(),
                request.projectDeadline(),
                request.submitDeadline(),
                request.crewType(),
                request.competency(),
                request.preferenceCondition(),
                getOrDefault(request.subsidy(), 0L),
                getOrDefault(request.incentive(), false),
                request.incentiveCondition(),
                request.additionalFileLinks(),
                request.referenceLink()
        );

        Project savedProject = projectRepository.save(project);
        return CompanyProjectIdResponse.from(savedProject);
    }

    @Transactional
    public CompanyProjectIdResponse createProjectDraft(Long companyId, CompanyProjectRequest request) {
        Company company = findActiveCompany(companyId);

        Project draft = Project.createDraft(
                company,
                request.projectImage(),
                request.brandName(),
                request.managerName(),
                request.managerEmail(),
                request.managerPhone(),
                request.name(),
                request.objectives(),
                request.projectType(),
                request.requirement(),
                request.projectExplanation(),
                request.resultForm(),
                request.essentialSubmitPart(),
                request.recruitDeadLine(),
                request.projectStartDate(),
                request.projectDeadline(),
                request.submitDeadline(),
                request.crewType(),
                request.competency(),
                request.preferenceCondition(),
                getOrDefault(request.subsidy(), 0L),
                getOrDefault(request.incentive(), false),
                request.incentiveCondition(),
                request.additionalFileLinks(),
                request.referenceLink()
        );

        Project savedDraft = projectRepository.save(draft);
        return CompanyProjectIdResponse.from(savedDraft);
    }

    @Transactional
    public CompanyProjectIdResponse updateProjectDraft(Long companyId, Long draftId, CompanyProjectRequest request) {
        Company company = findActiveCompany(companyId);
        Project draft = findCompanyDraft(company.getId(), draftId);

        draft.modifyDraft(
                getOrDefault(request.projectImage(), draft.getProjectImage()),
                getOrDefault(request.brandName(), draft.getBrandName()),
                getOrDefault(request.managerName(), draft.getManagerName()),
                getOrDefault(request.managerEmail(), draft.getManagerEmail()),
                getOrDefault(request.managerPhone(), draft.getManagerPhone()),
                getOrDefault(request.name(), draft.getName()),
                getOrDefault(request.objectives(), draft.getObjectives()),
                getOrDefault(request.projectType(), draft.getProjectType()),
                getOrDefault(request.requirement(), draft.getRequirement()),
                getOrDefault(request.projectExplanation(), draft.getProjectExplanation()),
                getOrDefault(request.resultForm(), draft.getResultForm()),
                getOrDefault(request.essentialSubmitPart(), draft.getEssentialSubmitPart()),
                getOrDefault(request.recruitDeadLine(), draft.getRecruitDeadLine()),
                getOrDefault(request.projectStartDate(), draft.getProjectStartDate()),
                getOrDefault(request.projectDeadline(), draft.getProjectDeadline()),
                getOrDefault(request.submitDeadline(), draft.getSubmitDeadline()),
                getOrDefault(request.crewType(), draft.getCrewType()),
                getOrDefault(request.competency(), draft.getCompetency()),
                getOrDefault(request.preferenceCondition(), draft.getPreferenceCondition()),
                getOrDefault(request.subsidy(), draft.getSubsidy()),
                getOrDefault(request.incentive(), draft.isIncentive()),
                getOrDefault(request.incentiveCondition(), draft.getIncentiveCondition()),
                getOrDefault(request.additionalFileLinks(), draft.getAdditionalFileLinks()),
                getOrDefault(request.referenceLink(), draft.getReferenceLink())
        );

        return CompanyProjectIdResponse.from(draft);
    }

    @Transactional(readOnly = true)
    public CompanyProjectDraftResponse getProjectDraft(Long companyId, Long draftId) {
        Company company = findActiveCompany(companyId);
        Project draft = findCompanyDraft(company.getId(), draftId);

        return CompanyProjectDraftResponse.from(draft);
    }

    @Transactional
    public void deleteProject(Long companyId, Long projectId) {
        Company company = findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<CompanyProjectApplicationResponse> getProjectApplications(
            Long companyId,
            Long projectId
    ) {
        Company company = findActiveCompany(companyId);
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
        Company company = findActiveCompany(companyId);
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
        Company company = findActiveCompany(companyId);
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

        return CompanyProjectApplicationSelectResponse.of(project, selectedApplication);
    }

    @Transactional(readOnly = true)
    public CompanyPartnerCrewResponse getPartnerCrew(Long companyId, Long projectId) {
        Company company = findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        Crew partnerCrew = findPartnerCrew(project);

        return CompanyPartnerCrewResponse.of(project, partnerCrew);
    }

    @Transactional
    public CompanyProjectRevisionResponse requestProjectRevision(
            Long companyId,
            Long projectId,
            CompanyProjectRevisionRequest request
    ) {
        Company company = findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);
        findPartnerCrew(project);

        ProjectSubmission submission = findLatestSubmission(project.getId());

        if (!submission.isSubmitted()) {
            throw new CustomException(ErrorCode.INVALID_SUBMISSION_STATUS);
        }

        submission.requestRevision(request.revisionReason());
        project.requestRevision();

        return CompanyProjectRevisionResponse.of(project, submission);
    }

    @Transactional
    public CompanyProjectApprovalResponse approveProject(
            Long companyId,
            Long projectId
    ) {
        Company company = findActiveCompany(companyId);
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

    private void rejectOtherApplications(Long projectId, Long selectedApplicationId) {
        List<ProjectApplication> pendingApplications =
                projectApplicationRepository.findAllByProjectIdAndStatus(
                        projectId,
                        ProjectApplicationStatus.PENDING
                );

        pendingApplications.stream()
                .filter(application -> !application.getId().equals(selectedApplicationId))
                .forEach(ProjectApplication::reject);
    }

    private Company findActiveCompany(Long companyId) {
        return companyRepository.findByIdAndStatus(companyId, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));
    }

    private Project findCompanyProject(Long companyId, Long projectId) {
        return projectRepository.findByIdAndCompanyId(projectId, companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private Project findCompanyDraft(Long companyId, Long draftId) {
        return projectRepository.findByIdAndCompanyIdAndStatus(draftId, companyId, ProjectStatus.DRAFT)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private <T> T getOrDefault(T newValue, T currentValue) {
        if (newValue == null) {
            return currentValue;
        }

        return newValue;
    }

    @Transactional(readOnly = true)
    public CompanyWorkspaceProjectDetailResponse getProjectReviewDetail(Long companyId, Long projectId) {
        Company company = findActiveCompany(companyId);
        Project project = findCompanyProject(company.getId(), projectId);

        if (project.getStatus() != ProjectStatus.INSPECTION) {
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        return CompanyWorkspaceProjectDetailResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<CompanySettlementResponse> getSettlements(
            Long companyId,
            ProjectSettlementStatus status
    ) {
        Company company = findActiveCompany(companyId);

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
        Company company = findActiveCompany(companyId);
        ProjectSettlement settlement = findCompanySettlement(company.getId(), settlementId);

        settlement.updateExpectedPaymentDate(request.expectedPaymentDate());

        return CompanySettlementExpectedPaymentDateResponse.from(settlement);
    }

    private ProjectSettlement findCompanySettlement(Long companyId, Long settlementId) {
        return projectSettlementRepository.findByIdAndCompanyId(settlementId, companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));
    }
}