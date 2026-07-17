package com.conx.server.user.dto.company.request;

import com.conx.server.user.domain.types.Industry;
import com.fasterxml.jackson.annotation.JsonAlias;

public record CompanyProfileUpdateRequest(
        String companyName,
        String companyIntroduction,
        String brandName,
        Industry industry,
        String customIndustry,
        String profileImage,
        String additionalFileLink,

        @JsonAlias("homepageLink")
        String website,

        String businessRegistrationNumber
) {
}