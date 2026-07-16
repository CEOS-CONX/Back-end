package com.conx.server.user.domain.company;

import com.conx.server.user.domain.User;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends User {

    private Company(String email, String password) {
        super(email, password);
    }

    private String companyName;

    private String companyIntroduction;

    private String brandName;

    /*
     * customIndustry는 Industry가 ETC인 경우 사용한다.
     * 프로필 응답에서는 제외하더라도 내부 데이터와 수정 요청에서는 유지한다.
     */
    @Enumerated(EnumType.STRING)
    private Industry industry;

    private String customIndustry;

    private String managerName;

    private String job;

    private String businessRegistrationNumber;

    private String profileImage;

    private String additionalFileLink;

    /*
     * DB 필드명은 기존 호환성을 위해 homepageLink로 유지한다.
     * API DTO에서는 website라는 이름으로 사용한다.
     */
    private String homepageLink;

    /*
     * 기업 공용 연락처.
     * 로그인 이메일과 구분되는 대표 전화번호 및 대표 이메일이다.
     */
    private String representativePhone;

    private String representativeEmail;

    public static Company create(String email, String password) {
        return new Company(email, password);
    }

    public void activateCompany(
            String brandName,
            Industry industry,
            String customIndustry,
            String managerName,
            String job
    ) {
        this.brandName = brandName;
        this.industry = industry;
        this.customIndustry = customIndustry;
        this.managerName = managerName;
        this.job = job;
        super.activate(UserRole.COMPANY);
    }

    public void modifyProfile(
            String companyName,
            String brandName,
            Industry industry,
            String customIndustry,
            String companyIntroduction,
            String homepageLink,
            String additionalFileLink,
            String profileImageLink,
            String businessRegistrationNumber
    ) {
        this.companyName = companyName;
        this.brandName = brandName;
        this.industry = industry;
        this.customIndustry = customIndustry;
        this.companyIntroduction = companyIntroduction;
        this.homepageLink = homepageLink;
        this.additionalFileLink = additionalFileLink;
        this.profileImage = profileImageLink;
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public void changeManagerName(String managerName) {
        this.managerName = managerName;
    }

    public void changeJob(String job) {
        this.job = job;
    }

    public void changeRepresentativePhone(String representativePhone) {
        this.representativePhone = representativePhone;
    }

    public void changeRepresentativeEmail(String representativeEmail) {
        this.representativeEmail = representativeEmail;
    }
}