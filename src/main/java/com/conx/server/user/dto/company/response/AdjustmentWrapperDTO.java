package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record AdjustmentWrapperDTO(
        long projectId,

        ProjectStatus projectStatus,
        long subsidy,
        String projectName,
        String brandName,
        LocalDate adjustedDate
) {
    public static AdjustmentWrapperDTO from(Project p, ProjectSettlement ps){
        return new AdjustmentWrapperDTO(
                p.getId(), p.getStatus(), p.getSubsidy(), p.getProjectName(), p.getBrandName(), ps.getPaymentDate()
        );
    }
}
