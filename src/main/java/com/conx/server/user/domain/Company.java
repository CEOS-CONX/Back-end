package com.conx.server.user.domain;

import com.conx.server.user.domain.types.Industry;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {
    private Company(User user, String brandName, Industry industry, String customIndustry,
                    String managerName, String job){
        this.user = user;
        this.brandName = brandName;
        this.industry = industry;
        this.customIndustry = customIndustry;
        this.managerName = managerName;
        this.job = job;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String brandName;

    //customIndustry는 Industry가 ETC(기타)인 경우 설정됨
    //나중에 Industry가 ETC인 경우에는 customIndustry를 사용해야함
    @Enumerated(EnumType.STRING)
    private Industry industry;
    private String customIndustry;

    private String managerName;

    private String job;

    private String businessRegistrationNumber;

    public static Company create(User user, String brandName, Industry industry,
                                 String customIndustry, String managerName, String job){
        return new Company(user, brandName, industry, customIndustry, managerName, job);
    }
}
