package com.conx.server.project.dto.response;

import com.conx.server.user.domain.crew.Crew;

import java.time.LocalDate;

public record CrewInfoForProjectApplicationDTO(
        String crewName,
        String managerName,
        LocalDate editedTime,
        boolean isEditDone
) {
    public static CrewInfoForProjectApplicationDTO create(Crew crew){
        return new CrewInfoForProjectApplicationDTO(
                crew.getCrewName(), crew.getManagerName(),
                crew.getUpdatedAt().toLocalDate(), true
        );
    }
}
//TODO:: 멀티프로필 기능 추가되면 수정해야함
