package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewEvaluation;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CompanyPartnerCrewResponse(
        ProjectStatus projectStatus,
        long projectId,

        long crewId,
        String projectImage,
        String crewName,
        String catchPhrase,
        Industry interestingIndustry,
        CrewType crewType,
        double point,
        int totalSubsidy
) {

    public static CompanyPartnerCrewResponse of(Project project, Crew crew, CrewEvaluation evaluation) {
        return new CompanyPartnerCrewResponse(
            project.getStatus(), project.getId(),
                crew.getId(), crew.getProfileImage(), crew.getCrewName(), crew.getCatchphrase(),
                crew.getInterestingIndustry(), crew.getCrewType(), evaluation.getMeanPoint(), crew.getTotalSubsidy()
        );
    }
}