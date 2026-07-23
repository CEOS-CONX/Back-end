package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;

import java.util.List;

public record CompanyWorkSpaceForProjectApplicationDTO(
        long applicationId,

        //크루정보
        long crewId,
        String crewName,
        String crewImageLink,
        CrewType crewType,
        String motivation,
        List<String> keywords
) {
    public static CompanyWorkSpaceForProjectApplicationDTO from(ProjectApplication pa){
        Crew crew = pa.getCrew();

        return new CompanyWorkSpaceForProjectApplicationDTO(pa.getId(), crew.getId(), crew.getCrewName(),
                crew.getProfileImage(), crew.getCrewType(), pa.getMotivation(), crew.getAdvantages());
    }
}
