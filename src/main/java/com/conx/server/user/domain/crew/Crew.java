package com.conx.server.user.domain;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew {
    private Crew(User user, CrewType crewType, String customCrewType,
                 String managerName, String job){
        this.user = user;
        this.crewType = crewType;
        this.customCrewType = customCrewType;
        this.managerName = managerName;
        this.job = job;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    private String crewName;

    //customCrewType은 crewType이 ETC(기타)인 경우 설정됨
    //나중에 crewType이 ETC인 경우에는 customCrewType을 사용해야함
    @Enumerated(EnumType.STRING)
    private CrewType crewType;
    private String customCrewType;

    private String managerName;

    private String job;

    private String profileImage;

    private String crewSchool;

    private int memberAmount;

    private Industry interestingIndustry;

    private String channel;

    private String crewIntroduction;

    private String advantages;

    private String snsLink;

    private String etcLink;

    private String kakaotalkLink;

    public static Crew create(User user, CrewType crewType, String customCrewType,
                              String managerName, String job){
        return new Crew(user, crewType, customCrewType, managerName, job);
    }

    //TODO: S3 연결 후 기본프로필 이미지사진 연결
}
