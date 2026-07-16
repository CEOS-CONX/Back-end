package com.conx.server.user.dto.company.response;

import java.util.List;

public record CompanyWorkspaceDashboardResponse(
        CompanyProjectStatusResponseDTO projectStatus,
        CompanyExpenditureStatusResponseDTO expenditureStatus,
        List<CompanyTodoProjectResponseDTO> todoProjectsStatus
) {

    public static CompanyWorkspaceDashboardResponse of(
            CompanyProjectStatusResponseDTO projectStatus,
            CompanyExpenditureStatusResponseDTO expenditureStatus,
            List<CompanyTodoProjectResponseDTO> todoProjectsStatus
    ) {
        return new CompanyWorkspaceDashboardResponse(
                projectStatus, expenditureStatus, todoProjectsStatus
        );
    }
}