package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

public record CompanyProjectApplicationResponse(
        Long applicationId,
        Long crewId,
        String crewName,
        String profileImage,
        CrewType crewType,
        Industry interestingIndustry,
        ProjectApplicationStatus status
) {

    public static CompanyProjectApplicationResponse from(ProjectApplication application) {
        Crew crew = application.getCrew();

        return new CompanyProjectApplicationResponse(
                application.getId(),
                crew.getId(),
                crew.getCrewName(),
                crew.getProfileImage(),
                crew.getCrewType(),
                crew.getInterestingIndustry(),
                application.getStatus()
        );
    }
}