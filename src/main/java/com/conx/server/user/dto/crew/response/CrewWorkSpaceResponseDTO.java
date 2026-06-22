package com.conx.server.user.dto.crew.response;

import java.util.List;

public record CrewWorkSpaceResponseDTO(
    List<ProjectWrapperForCrewWorkSpaceDTO> projects
) {
}
