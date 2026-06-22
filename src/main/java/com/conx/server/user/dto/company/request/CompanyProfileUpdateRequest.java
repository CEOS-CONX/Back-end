package com.conx.server.user.dto.company.request;

import com.conx.server.user.domain.types.Industry;

public record CompanyProfileUpdateRequest(
        String companyName,
        String companyIntroduction,
        String brandName,
        Industry industry,
        String customIndustry,
        String profileImage,
        String additionalFileLink,
        String homepageLink
) {
}