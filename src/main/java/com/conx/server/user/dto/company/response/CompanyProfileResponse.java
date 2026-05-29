package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.Industry;

public record CompanyProfileResponse(
        String companyName,
        String companyIntroduction,
        String brandName,
        Industry industry,
        String customIndustry,
        String profileImage,
        String additionalFileLink,
        String homepageLink
) {

    public static CompanyProfileResponse from(Company company) {
        return new CompanyProfileResponse(
                company.getCompanyName(),
                company.getCompanyIntroduction(),
                company.getBrandName(),
                company.getIndustry(),
                company.getCustomIndustry(),
                company.getProfileImage(),
                company.getAdditionalFileLink(),
                company.getHomepageLink()
        );
    }
}