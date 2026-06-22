package com.conx.server.landingPage.dto;

import java.util.List;

public record AnonymousLandingPageResponseDTO(
        List<ProjectWrapperForLandingPageDTO> projects,
        List<CrewWrapperForLandingPageDTO> crews
) {

}
