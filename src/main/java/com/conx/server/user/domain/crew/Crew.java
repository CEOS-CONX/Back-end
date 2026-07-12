package com.conx.server.user.domain.crew;

import com.conx.server.user.domain.User;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends User {

    private Crew(
            String email,
            String password
    ) {
        super(email, password);
    }

    private String crewName;

    @Enumerated(EnumType.STRING)
    private CrewType crewType;

    private String customCrewType;

    private String managerName;

    private String managerPhoneNumber;

    private String job;

    private String profileImage;

    /*
     * 기존 데이터 호환을 위해 유지합니다.
     * 신규 데이터는 schools를 우선 사용합니다.
     */
    private String crewSchool;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "crew_schools",
            joinColumns = @JoinColumn(name = "crew_id")
    )
    @Column(name = "school")
    private List<String> schools = new ArrayList<>();

    private int memberAmount;

    private String additionalIntroduction;

    @Enumerated(EnumType.STRING)
    private Industry interestingIndustry;

    private String channel;

    private String crewIntroduction;

    private List<String> advantages;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "crew_specialties",
            joinColumns = @JoinColumn(name = "crew_id")
    )
    @Column(name = "specialty")
    private List<String> specialties = new ArrayList<>();

    /*
     * 기존 데이터 호환을 위해 유지합니다.
     * 신규 데이터는 CrewLink Entity를 사용합니다.
     */
    private String snsLink;

    private String etcLink;

    private String kakaotalkLink;

    private int totalSubsidy;

    private int totalProjectCount;

    public void plusTotalProjectCount() {
        totalProjectCount++;
    }

    public void completeAdjustment(int subsidy) {
        totalSubsidy += subsidy;
    }

    public static Crew create(
            String email,
            String password
    ) {
        return new Crew(
                email,
                password
        );
    }

    public void activateCrew(
            String crewName,
            CrewType crewType,
            String customCrewType,
            String managerName,
            String job
    ) {
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

    public void replaceSchools(List<String> newSchools) {
        if (newSchools == null) {
            return;
        }

        schools.clear();

        schools.addAll(
                normalizeStringList(newSchools)
        );
    }

    public void replaceSpecialties(
            List<String> newSpecialties
    ) {
        if (newSpecialties == null) {
            return;
        }

        specialties.clear();

        specialties.addAll(
                normalizeStringList(newSpecialties)
        );
    }

    public List<String> getPublicSchools() {
        if (schools != null && !schools.isEmpty()) {
            return List.copyOf(schools);
        }

        if (hasText(crewSchool)) {
            return List.of(crewSchool.trim());
        }

        return List.of();
    }

    public List<String> getPublicSpecialties() {
        if (specialties == null) {
            return List.of();
        }

        return List.copyOf(specialties);
    }

    /*
     * 링크, 자료, 포트폴리오, 프로젝트는 별도 Entity이므로
     * 최종 hasPublicDetail은 Service에서 함께 계산합니다.
     */
    public boolean hasPublicProfileContent() {
        return !getPublicSchools().isEmpty()
                || hasText(crewIntroduction)
                || hasText(additionalIntroduction)
                || hasItems(advantages)
                || !getPublicSpecialties().isEmpty()
                || hasText(snsLink)
                || hasText(etcLink)
                || hasText(kakaotalkLink);
    }

    private List<String> normalizeStringList(
            List<String> values
    ) {
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .toList();
    }

    private boolean hasItems(List<?> values) {
        return values != null && !values.isEmpty();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}