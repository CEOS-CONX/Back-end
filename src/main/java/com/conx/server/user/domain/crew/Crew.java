package com.conx.server.user.domain.crew;

import com.conx.server.user.domain.User;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends User {
    private Crew(String email, String password){
        super(email, password);
    }

    private String crewName;

    //customCrewType은 crewType이 ETC(기타)인 경우 설정됨
    //나중에 crewType이 ETC인 경우에는 customCrewType을 사용해야함
    @Enumerated(EnumType.STRING)
    private CrewType crewType;
    private String customCrewType;

    private String managerName;

    private String managerPhoneNumber;
    //ERD 반영하기

    private String job;

    private String profileImage;

    private String crewSchool;

    private int memberAmount;

    private String additionalIntroduction;
    //ERD 반영하기

    private Industry interestingIndustry;

    private String channel;

    private String crewIntroduction;

    @ElementCollection
    @CollectionTable(
            name = "crew_advantages",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<String> advantages;

    private String snsLink;

    private String etcLink;

    private String kakaotalkLink;

    private int totalSubsidy;

    private int totalProjectCount;
    //누적 프로젝트 수

    public void plusTotalProjectCount(){
        totalProjectCount++;
    }

    public void completeAdjustment(int subsidy){
        totalSubsidy += subsidy;
    }
    //TODO: 어드민 정산 기능 후에 위 메서드 두 개 호출하여 최종 프로젝트 결과(누적 프로젝트 수 및 지원금 총액 증가)를 반영하기


    public static Crew create(String email, String password){
        return new Crew(email, password);
    }

    public void activateCrew(String crewName, CrewType crewType,
                             String customCrewType, String managerName, String job){
        this.crewName = crewName;
        this.crewType = crewType;
        this.customCrewType = customCrewType;
        this.managerName = managerName;
        this.job = job;
        super.activate(UserRole.CREW);
    }

    public void modifyMyPageProfile(
            String profileImage,
            String crewName,
            CrewType crewType,
            String customCrewType,
            String managerName,
            String job,
            String crewSchool,
            Integer memberAmount,
            String crewIntroduction,
            String additionalIntroduction,
            List<String> advantages,
            Industry interestingIndustry,
            String snsLink,
            String etcLink,
            String kakaotalkLink
    ) {
        this.profileImage = profileImage;
        this.crewName = crewName;
        this.crewType = crewType;
        this.customCrewType = customCrewType;
        this.managerName = managerName;
        this.job = job;
        this.crewSchool = crewSchool;
        this.memberAmount = memberAmount;
        this.crewIntroduction = crewIntroduction;
        this.additionalIntroduction = additionalIntroduction;
        this.advantages = advantages;
        this.interestingIndustry = interestingIndustry;
        this.snsLink = snsLink;
        this.etcLink = etcLink;
        this.kakaotalkLink = kakaotalkLink;
    }
}
