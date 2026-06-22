package com.conx.server.user.dto.crew.response;

public record CrewProjectInfoDTO(
        int appliedProjectAmount,
        int progressProjectAmount,
        int doneProjectAmount
) {
}
