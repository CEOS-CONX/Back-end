package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.response.ResultFormResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public record CrewProjectWorkspaceDetailResponse(
        long projectId,
        String projectName,
        String brandName,
        String companyName,
        String managerName,
        String managerEmail,
        String managerPhone,
        ProjectStatus projectStatus,
        List<ProgressStep> progressSteps,
        SettlementInfo settlement,
        SubmitCondition submitCondition
) {

    public enum ProgressStage {
        MATCHING_COMPLETED,
        IN_PROGRESS,
        EXECUTION_COMPLETED,
        SUBMISSION_COMPLETED,
        SETTLEMENT_COMPLETED
    }

    public enum ProgressState {
        COMPLETED,
        CURRENT,
        UPCOMING
    }

    public enum ProgressDateType {
        NONE,
        PLANNED,
        EXPECTED,
        ACTUAL
    }

    public record ProgressStep(
            ProgressStage stage,
            ProgressState state,
            LocalDate date,
            ProgressDateType dateType
    ) {
    }

    public record SettlementInfo(
            Long settlementId,
            long amount,
            ProjectSettlementStatus conxSettlementStatus,
            LocalDate expectedPaymentDate,
            LocalDate settlementDate,
            CrewPaymentStatus crewPaymentStatus,
            LocalDate crewPaymentConfirmedDate
    ) {
    }

    public record SubmitCondition(
            List<String> requirements,
            List<ResultFormResponse> resultForm,
            String essentialSubmitPart,
            LocalDate submitDeadline
    ) {
    }

    public static CrewProjectWorkspaceDetailResponse from(
            Project project,
            ProjectSettlement settlement,
            ProjectSubmission latestSubmission
    ) {
        return new CrewProjectWorkspaceDetailResponse(
                project.getId(),
                project.getProjectName(),
                project.getBrandName(),
                project.getCompany().getCompanyName(),
                project.getManagerName(),
                project.getManagerEmail(),
                null,
                project.getStatus(),
                createProgressSteps(
                        project,
                        settlement,
                        latestSubmission
                ),
                createSettlementInfo(
                        project,
                        settlement
                ),
                new SubmitCondition(
                        List.of(),
                        createResultFormResponses(
                                project
                        ),
                        null,
                        project.getSubmitDeadline()
                )
        );
    }

    private static SettlementInfo createSettlementInfo(
            Project project,
            ProjectSettlement settlement
    ) {
        if (settlement == null) {
            return new SettlementInfo(
                    null,
                    project.getSubsidy(),
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return new SettlementInfo(
                settlement.getId(),
                settlement.getSubsidy(),
                settlement.getStatus(),
                settlement.getExpectedPaymentDate(),
                settlement.getPaymentDate(),
                settlement.getResolvedCrewPaymentStatus(),
                settlement.getCrewPaymentConfirmedDate()
        );
    }

    private static List<ProgressStep>
    createProgressSteps(
            Project project,
            ProjectSettlement settlement,
            ProjectSubmission latestSubmission
    ) {
        int currentIndex =
                resolveCurrentStepIndex(
                        project.getStatus()
                );

        LocalDate submissionDate =
                latestSubmission == null
                        || latestSubmission
                        .getResolvedSubmittedAt() == null
                        ? null
                        : latestSubmission
                        .getResolvedSubmittedAt()
                        .toLocalDate();

        LocalDate settlementStepDate = null;
        ProgressDateType settlementDateType =
                ProgressDateType.NONE;

        if (
                settlement != null
                        && settlement.getPaymentDate()
                        != null
        ) {
            settlementStepDate =
                    settlement.getPaymentDate();

            settlementDateType =
                    ProgressDateType.ACTUAL;
        } else if (
                settlement != null
                        && settlement
                        .getExpectedPaymentDate()
                        != null
        ) {
            settlementStepDate =
                    settlement.getExpectedPaymentDate();

            settlementDateType =
                    ProgressDateType.EXPECTED;
        }

        return List.of(
                new ProgressStep(
                        ProgressStage.MATCHING_COMPLETED,
                        resolveStepState(
                                0,
                                currentIndex
                        ),
                        null,
                        ProgressDateType.NONE
                ),
                new ProgressStep(
                        ProgressStage.IN_PROGRESS,
                        resolveStepState(
                                1,
                                currentIndex
                        ),
                        project.getProjectStartDate(),
                        project.getProjectStartDate() == null
                                ? ProgressDateType.NONE
                                : ProgressDateType.PLANNED
                ),
                new ProgressStep(
                        ProgressStage.EXECUTION_COMPLETED,
                        resolveStepState(
                                2,
                                currentIndex
                        ),
                        project.getProjectDeadline(),
                        project.getProjectDeadline() == null
                                ? ProgressDateType.NONE
                                : ProgressDateType.PLANNED
                ),
                new ProgressStep(
                        ProgressStage.SUBMISSION_COMPLETED,
                        resolveStepState(
                                3,
                                currentIndex
                        ),
                        submissionDate == null
                                ? project.getSubmitDeadline()
                                : submissionDate,
                        submissionDate == null
                                ? project.getSubmitDeadline() == null
                                ? ProgressDateType.NONE
                                : ProgressDateType.PLANNED
                                : ProgressDateType.ACTUAL
                ),
                new ProgressStep(
                        ProgressStage.SETTLEMENT_COMPLETED,
                        resolveStepState(
                                4,
                                currentIndex
                        ),
                        settlementStepDate,
                        settlementDateType
                )
        );
    }

    private static int resolveCurrentStepIndex(
            ProjectStatus status
    ) {
        return switch (status) {
            case CONTRACT_PENDING -> 0;
            case PROGRESS -> 1;
            case WAITING_RESULT -> 2;
            case INSPECTION, ADJUSTING -> 3;
            case DONE -> 4;
            case DRAFT, RECRUITING, EXPIRED -> 0;
            default -> 0;
        };
    }

    private static ProgressState resolveStepState(
            int stepIndex,
            int currentIndex
    ) {
        if (stepIndex < currentIndex) {
            return ProgressState.COMPLETED;
        }

        if (stepIndex == currentIndex) {
            return ProgressState.CURRENT;
        }

        return ProgressState.UPCOMING;
    }

    private static List<String> splitRequirements(
            String requirement
    ) {
        if (
                requirement == null
                        || requirement.isBlank()
        ) {
            return List.of();
        }

        return Arrays.stream(
                        requirement.split(",")
                )
                .map(String::trim)
                .filter(value ->
                        !value.isBlank()
                )
                .toList();
    }

    private static List<ResultFormResponse>
    createResultFormResponses(
            Project project
    ) {
        if (
                project.getResultForm() == null
                        || project.getResultForm().isEmpty()
        ) {
            return List.of();
        }

        return project.getResultForm()
                .stream()
                .map(ResultFormResponse::from)
                .toList();
    }
}
