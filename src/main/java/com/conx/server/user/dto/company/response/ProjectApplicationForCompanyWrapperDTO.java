package com.conx.server.user.dto.company.response;

import java.util.List;

public record ProjectApplicationForCompanyWrapperDTO(
        String status,
        DetailedProjectResponseDTO common,
        List<CompanyWorkSpaceForProjectApplicationDTO> applications


)implements CompanyWorkspaceProjectDetailResponse {
    public static ProjectApplicationForCompanyWrapperDTO from(DetailedProjectResponseDTO common,
                                                              List<CompanyWorkSpaceForProjectApplicationDTO> applications){
        return new ProjectApplicationForCompanyWrapperDTO("RECRUITING",common,  applications);
    }
}
