package com.conx.server.user.dto.company.response;

import java.util.List;

public record ProjectApplicationForCompanyWrapperDTO(
        CompanyProjectDetailResponse common,
        List<CompanyWorkSpaceForProjectApplicationDTO> applications


)implements CompanyWorkspaceProjectDetailResponse {
    public static ProjectApplicationForCompanyWrapperDTO from(CompanyProjectDetailResponse common,
                                                              List<CompanyWorkSpaceForProjectApplicationDTO> applications){
        return new ProjectApplicationForCompanyWrapperDTO(common,  applications);
    }
}
