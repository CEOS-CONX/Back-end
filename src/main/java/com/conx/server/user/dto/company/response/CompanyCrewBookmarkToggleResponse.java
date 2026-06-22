package com.conx.server.user.dto.company.response;

public record CompanyCrewBookmarkToggleResponse(
        Long crewId,
        boolean bookmarked
) {

    public static CompanyCrewBookmarkToggleResponse of(Long crewId, boolean bookmarked) {
        return new CompanyCrewBookmarkToggleResponse(crewId, bookmarked);
    }
}