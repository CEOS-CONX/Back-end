package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.Industry;

public record CompanyProfileResponse(
        String companyName,
        String companyIntroduction,
        String brandName,
        Industry industry,
        String businessRegistrationNumber,
        String profileImage,
        String additionalFileLink,
        String website
) {

    public static CompanyProfileResponse from(Company company) {
        return new CompanyProfileResponse(
                company.getCompanyName(),
                company.getCompanyIntroduction(),
                company.getBrandName(),
                company.getIndustry(),
                company.getBusinessRegistrationNumber(),
                company.getProfileImage(),
                company.getAdditionalFileLink(),
                company.getHomepageLink()
        );
    }
}