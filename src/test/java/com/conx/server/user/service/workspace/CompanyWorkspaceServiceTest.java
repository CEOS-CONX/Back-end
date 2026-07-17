package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.notification.service.notificationFactory.NotificationFacadeService;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.repository.ProjectSettlementRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.dto.company.request.CompanyProjectEvaluationRequest;
import com.conx.server.user.dto.company.request.CompanySettlementCompleteRequest;
import com.conx.server.user.dto.company.response.CompanyProjectEvaluationResponse;
import com.conx.server.user.dto.company.response.CompanySettlementCompleteResponse;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.service.common.UserFinder;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.project.service.CrewProjectTodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyWorkspaceServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectApplicationRepository projectApplicationRepository;

    @Mock
    private ProjectSubmissionRepository projectSubmissionRepository;

    @Mock
    private ProjectSettlementRepository projectSettlementRepository;

    @Mock
    private NotificationFacadeService notificationFacadeService;

    @Mock
    private UserFinder userFinder;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private Company company;

    @Mock
    private Crew crew;

    @Mock
    private Project project;

    @Mock
    private ProjectSettlement settlement;

    @Mock
    private CrewProjectTodoService crewProjectTodoService;

    @InjectMocks
    private CompanyWorkspaceService companyWorkspaceService;

    @Test
    @DisplayName("기업은 승인된 프로젝트의 크루를 평가할 수 있다")
    void evaluateProject() {
        // given
        Long companyId = 10L;
        Long projectId = 100L;

        CompanyProjectEvaluationRequest request =
                new CompanyProjectEvaluationRequest(
                        5,
                        4,
                        5,
                        4,
                        5
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectRepository.findByIdAndCompanyId(
                        projectId,
                        companyId
                )
        ).willReturn(Optional.of(project));

        given(project.getId())
                .willReturn(projectId);

        given(project.getStatus())
                .willReturn(ProjectStatus.ADJUSTING);

        given(project.getSelectedCrew())
                .willReturn(crew);

        given(crew.getId())
                .willReturn(1L);

        given(
                evaluationRepository.existsByProjectId(projectId)
        ).willReturn(false);

        given(
                evaluationRepository.save(any(Evaluation.class))
        ).willAnswer(invocation ->
                invocation.getArgument(0)
        );

        // when
        CompanyProjectEvaluationResponse response =
                companyWorkspaceService.evaluateProject(
                        companyId,
                        projectId,
                        request
                );

        // then
        assertThat(response.projectId())
                .isEqualTo(projectId);

        assertThat(response.companyId())
                .isEqualTo(companyId);

        assertThat(response.crewId())
                .isEqualTo(1L);

        assertThat(response.completeness())
                .isEqualTo(5);

        assertThat(response.schedule())
                .isEqualTo(4);

        assertThat(response.ability())
                .isEqualTo(5);

        assertThat(response.recooperation())
                .isEqualTo(4);

        assertThat(response.communication())
                .isEqualTo(5);

        assertThat(response.mean())
                .isEqualTo(4.6);

        ArgumentCaptor<Evaluation> evaluationCaptor =
                ArgumentCaptor.forClass(Evaluation.class);

        verify(evaluationRepository)
                .save(evaluationCaptor.capture());

        Evaluation savedEvaluation =
                evaluationCaptor.getValue();

        assertThat(savedEvaluation.getProject())
                .isEqualTo(project);

        assertThat(savedEvaluation.getCrew())
                .isEqualTo(crew);

        assertThat(savedEvaluation.getCompany())
                .isEqualTo(company);

        assertThat(savedEvaluation.getMean())
                .isEqualTo(4.6);
    }

    @Test
    @DisplayName("결과물이 승인되지 않은 프로젝트는 평가할 수 없다")
    void cannotEvaluateProjectBeforeApproval() {
        // given
        Long companyId = 10L;
        Long projectId = 100L;

        CompanyProjectEvaluationRequest request =
                new CompanyProjectEvaluationRequest(
                        5,
                        4,
                        5,
                        4,
                        5
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectRepository.findByIdAndCompanyId(
                        projectId,
                        companyId
                )
        ).willReturn(Optional.of(project));

        given(project.getStatus())
                .willReturn(ProjectStatus.PROGRESS);

        // when & then
        assertThatThrownBy(() ->
                companyWorkspaceService.evaluateProject(
                        companyId,
                        projectId,
                        request
                )
        ).isInstanceOf(CustomException.class);

        verify(
                evaluationRepository,
                never()
        ).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("이미 평가한 프로젝트는 다시 평가할 수 없다")
    void cannotEvaluateProjectTwice() {
        // given
        Long companyId = 10L;
        Long projectId = 100L;

        CompanyProjectEvaluationRequest request =
                new CompanyProjectEvaluationRequest(
                        5,
                        4,
                        5,
                        4,
                        5
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectRepository.findByIdAndCompanyId(
                        projectId,
                        companyId
                )
        ).willReturn(Optional.of(project));

        given(project.getId())
                .willReturn(projectId);

        given(project.getStatus())
                .willReturn(ProjectStatus.ADJUSTING);

        given(project.getSelectedCrew())
                .willReturn(crew);

        given(
                evaluationRepository.existsByProjectId(projectId)
        ).willReturn(true);

        // when & then
        assertThatThrownBy(() ->
                companyWorkspaceService.evaluateProject(
                        companyId,
                        projectId,
                        request
                )
        ).isInstanceOf(CustomException.class);

        verify(
                evaluationRepository,
                never()
        ).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("선정된 크루가 없는 프로젝트는 평가할 수 없다")
    void cannotEvaluateProjectWithoutSelectedCrew() {
        // given
        Long companyId = 10L;
        Long projectId = 100L;

        CompanyProjectEvaluationRequest request =
                new CompanyProjectEvaluationRequest(
                        5,
                        4,
                        5,
                        4,
                        5
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectRepository.findByIdAndCompanyId(
                        projectId,
                        companyId
                )
        ).willReturn(Optional.of(project));

        given(project.getStatus())
                .willReturn(ProjectStatus.ADJUSTING);

        given(project.getSelectedCrew())
                .willReturn(null);

        // when & then
        assertThatThrownBy(() ->
                companyWorkspaceService.evaluateProject(
                        companyId,
                        projectId,
                        request
                )
        ).isInstanceOf(CustomException.class);

        verify(
                evaluationRepository,
                never()
        ).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("기업은 대기 중인 정산을 지급 완료할 수 있다")
    void completeSettlement() {
        // given
        Long companyId = 10L;
        Long settlementId = 500L;
        Long projectId = 100L;
        LocalDate settlementDate =
                LocalDate.of(2026, 7, 14);

        CompanySettlementCompleteRequest request =
                new CompanySettlementCompleteRequest(
                        settlementDate
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectSettlementRepository
                        .findByIdAndCompanyIdForUpdate(
                                settlementId,
                                companyId
                        )
        ).willReturn(Optional.of(settlement));

        /*
         * 첫 번째 호출은 완료 처리 전 상태 검증,
         * 두 번째 호출은 응답 DTO 생성 시 사용됩니다.
         */
        given(settlement.getStatus())
                .willReturn(
                        ProjectSettlementStatus.WAITING,
                        ProjectSettlementStatus.PAID
                );

        given(settlement.getProject())
                .willReturn(project);

        given(settlement.getCrew())
                .willReturn(crew);

        given(settlement.getId())
                .willReturn(settlementId);

        given(settlement.getPaymentDate())
                .willReturn(settlementDate);

        /*
         * 첫 번째 호출은 상태 검증,
         * 두 번째 호출은 응답 DTO 생성 시 사용됩니다.
         */
        given(project.getStatus())
                .willReturn(
                        ProjectStatus.ADJUSTING,
                        ProjectStatus.DONE
                );

        given(project.getId())
                .willReturn(projectId);

        // when
        CompanySettlementCompleteResponse response =
                companyWorkspaceService.completeSettlement(
                        companyId,
                        settlementId,
                        request
                );

        // then
        assertThat(response.settlementId())
                .isEqualTo(settlementId);

        assertThat(response.projectId())
                .isEqualTo(projectId);

        assertThat(response.settlementStatus())
                .isEqualTo(ProjectSettlementStatus.PAID);

        assertThat(response.projectStatus())
                .isEqualTo(ProjectStatus.DONE);

        assertThat(response.settlementDate())
                .isEqualTo(settlementDate);

        verify(
                projectSettlementRepository
        ).findByIdAndCompanyIdForUpdate(
                settlementId,
                companyId
        );

        verify(settlement)
                .markAsPaid(settlementDate);

        verify(project)
                .completeSettlement();

        verify(crewProjectTodoService)
                .completeIfExists(
                        crew,
                        project,
                        CrewProjectTodoType.SETTLEMENT_CONFIRMATION
                );

        verify(crew, never())
                .completeAdjustment(anyInt());
    }

    @Test
    @DisplayName("이미 지급 완료된 정산은 다시 완료할 수 없다")
    void cannotCompleteAlreadyPaidSettlement() {
        // given
        Long companyId = 10L;
        Long settlementId = 500L;

        CompanySettlementCompleteRequest request =
                new CompanySettlementCompleteRequest(
                        LocalDate.of(2026, 7, 14)
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectSettlementRepository
                        .findByIdAndCompanyIdForUpdate(
                                settlementId,
                                companyId
                        )
        ).willReturn(Optional.of(settlement));

        given(settlement.getStatus())
                .willReturn(ProjectSettlementStatus.PAID);

        // when & then
        assertThatThrownBy(() ->
                companyWorkspaceService.completeSettlement(
                        companyId,
                        settlementId,
                        request
                )
        ).isInstanceOf(CustomException.class);

        verify(settlement, never())
                .markAsPaid(any(LocalDate.class));

        verify(project, never())
                .completeSettlement();

        verify(
                crewProjectTodoService,
                never()
        ).completeIfExists(
                any(Crew.class),
                any(Project.class),
                any(CrewProjectTodoType.class)
        );
    }

    @Test
    @DisplayName("정산 중 상태가 아닌 프로젝트는 지급 완료할 수 없다")
    void cannotCompleteSettlementWhenProjectIsNotAdjusting() {
        // given
        Long companyId = 10L;
        Long settlementId = 500L;

        CompanySettlementCompleteRequest request =
                new CompanySettlementCompleteRequest(
                        LocalDate.of(2026, 7, 14)
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectSettlementRepository
                        .findByIdAndCompanyIdForUpdate(
                                settlementId,
                                companyId
                        )
        ).willReturn(Optional.of(settlement));

        given(settlement.getStatus())
                .willReturn(ProjectSettlementStatus.WAITING);

        given(settlement.getProject())
                .willReturn(project);

        given(project.getStatus())
                .willReturn(ProjectStatus.PROGRESS);

        // when & then
        assertThatThrownBy(() ->
                companyWorkspaceService.completeSettlement(
                        companyId,
                        settlementId,
                        request
                )
        ).isInstanceOf(CustomException.class);

        verify(settlement, never())
                .markAsPaid(any(LocalDate.class));

        verify(project, never())
                .completeSettlement();

        verify(
                crewProjectTodoService,
                never()
        ).completeIfExists(
                any(Crew.class),
                any(Project.class),
                any(CrewProjectTodoType.class)
        );
    }

    @Test
    @DisplayName("기업 소유의 정산을 찾을 수 없으면 지급 완료할 수 없다")
    void cannotCompleteSettlementWhenSettlementNotFound() {
        // given
        Long companyId = 10L;
        Long settlementId = 500L;

        CompanySettlementCompleteRequest request =
                new CompanySettlementCompleteRequest(
                        LocalDate.of(2026, 7, 14)
                );

        given(userFinder.findActiveCompany(companyId))
                .willReturn(company);

        given(company.getId())
                .willReturn(companyId);

        given(
                projectSettlementRepository
                        .findByIdAndCompanyIdForUpdate(
                                settlementId,
                                companyId
                        )
        ).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyWorkspaceService.completeSettlement(
                        companyId,
                        settlementId,
                        request
                )
        ).isInstanceOf(CustomException.class);

        verify(settlement, never())
                .markAsPaid(any(LocalDate.class));

        verify(project, never())
                .completeSettlement();

        verify(
                crewProjectTodoService,
                never()
        ).completeIfExists(
                any(Crew.class),
                any(Project.class),
                any(CrewProjectTodoType.class)
        );
    }
}
