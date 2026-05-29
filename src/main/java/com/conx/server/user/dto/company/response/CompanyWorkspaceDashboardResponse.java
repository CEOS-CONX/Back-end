package com.conx.server.user.dto.company.response;

public record CompanyWorkspaceDashboardResponse(
        long totalProjectCount,
        long recruitingProjectCount
) {

    public static CompanyWorkspaceDashboardResponse of(
            long totalProjectCount,
            long recruitingProjectCount
    ) {
        return new CompanyWorkspaceDashboardResponse(
                totalProjectCount,
                recruitingProjectCount
        );
    }
}