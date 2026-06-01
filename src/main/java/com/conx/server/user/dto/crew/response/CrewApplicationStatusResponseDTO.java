package com.conx.server.user.dto.crew.response;

import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;

import java.util.List;

public record CrewApplicationStatusResponseDTO(
        List<ProjectApplicationWrapperDTO> applications
) {
}
