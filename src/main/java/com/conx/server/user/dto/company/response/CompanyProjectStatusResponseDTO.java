package com.conx.server.user.dto.company.response;

public record CompanyProjectStatusResponseDTO(
        long recruiting,
        long progress,
        long waiting_inspection,
        long waiting_settlement,
        long done
) {
}
