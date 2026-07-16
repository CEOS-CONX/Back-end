package com.conx.server.user.dto.company.response;

import org.springframework.data.domain.Page;

public record ProjectStatusResponseDTO (
        CompanyProjectDetailResponse common,
        Page<InspectionInfoInOneLineDTO> inspections
) implements CompanyWorkspaceProjectDetailResponse {
    public static ProjectStatusResponseDTO create(CompanyProjectDetailResponse common, Page<InspectionInfoInOneLineDTO> inspections){
        return new ProjectStatusResponseDTO(common, inspections);
    }
}
