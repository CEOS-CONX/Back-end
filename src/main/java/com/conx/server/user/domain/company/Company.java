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
    private Company(String email, String password){
        super(email, password);
    }

    private String companyName;

    private String companyIntroduction;

    private String brandName;

    //customIndustry는 Industry가 ETC(기타)인 경우 설정됨
    //나중에 Industry가 ETC인 경우에는 customIndustry를 사용해야함
    @Enumerated(EnumType.STRING)
    private Industry industry;
    private String customIndustry;

    private String managerName;

    private String job;

    private String businessRegistrationNumber;

    private String profileImage;

    private String additionalFileLink;

    private String homepageLink;

    private int totalExpenditure;

    public static Company create(String email, String password){
        return new Company(email, password);
    }

    public void activateCompany(String brandName, Industry industry,
                         String customIndustry, String managerName, String job){
        this.brandName = brandName;
        this.industry = industry;
        this.customIndustry = customIndustry;
        this.managerName = managerName;
        this.job = job;
        super.activate(UserRole.COMPANY);
        this.totalExpenditure = 0;
    }

    public void modifyProfile(String companyName, String brandName,
                              Industry industry, String customIndustry, String companyIntroduction,
                              String homepageLink, String additionalFileLink, String profileImageLink){
        this.companyName = companyName;
        this.brandName = brandName;
        this.industry = industry;
        this.customIndustry = customIndustry;
        this.companyIntroduction = companyIntroduction;
        this.homepageLink = homepageLink;
        this.additionalFileLink = additionalFileLink;
        this.profileImage = profileImageLink;
    }

    public void modifyAccount(String companyName, String businessRegistrationNumber,
                              String managerName, String job) {
        this.companyName = companyName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.managerName = managerName;
        this.job = job;
    }
}