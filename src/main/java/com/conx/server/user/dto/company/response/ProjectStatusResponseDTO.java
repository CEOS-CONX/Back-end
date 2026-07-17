package com.conx.server.user.dto.company.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProjectStatusResponseDTO (
        String status,
        DetailedProjectResponseDTO common,
        List<InspectionInfoInOneLineDTO> inspections
) implements CompanyWorkspaceProjectDetailResponse {
    public static ProjectStatusResponseDTO create(DetailedProjectResponseDTO common, Page<InspectionInfoInOneLineDTO> inspections){
        return new ProjectStatusResponseDTO("PROGRESS", common, inspections.getContent());
    }
}
