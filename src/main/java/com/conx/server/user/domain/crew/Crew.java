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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "crew_schools",
            joinColumns = @JoinColumn(name = "crew_id")
    )
    @Column(name = "school")
    private List<String> schools = new ArrayList<>();

    private int memberAmount;

    /*
     * 신규 크루 프로필의 활동 분야입니다.
     * 선택 옵션 확정 전까지 String으로 저장합니다.
     */
    private String activityField;

    @Column(length = 30)
    private String catchphrase;

    @Enumerated(EnumType.STRING)
    private Industry interestingIndustry;

    private String channel;

    private String crewIntroduction;

    @ElementCollection
    @CollectionTable(
            name = "crew_advantages",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<String> advantages;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "crew_specialties",
            joinColumns = @JoinColumn(name = "crew_id")
    )
    @Column(name = "specialty")
    private List<String> specialties = new ArrayList<>();

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

    /*
     * 마이페이지의 단일 값 필드만 수정합니다.
     * 배열 필드는 replace 메서드에서 별도로 처리합니다.
     */
    public void modifyMyPageProfile(
            String profileImage,
            String crewName,
            CrewType crewType,
            String customCrewType,
            String managerName,
            String job,
            String activityField,
            Integer memberAmount,
            String catchphrase,
            String crewIntroduction,
            Industry interestingIndustry
    ) {
        this.profileImage = profileImage;
        this.crewName = crewName;
        this.crewType = crewType;
        this.customCrewType = customCrewType;
        this.managerName = managerName;
        this.job = job;
        this.activityField = activityField;
        this.memberAmount = memberAmount;
        this.catchphrase = catchphrase;
        this.crewIntroduction = crewIntroduction;
        this.interestingIndustry = interestingIndustry;
    }

    public void replaceSchools(
            List<String> newSchools
    ) {
        if (newSchools == null) {
            return;
        }

        schools.clear();
        schools.addAll(
                normalizeStringList(newSchools)
        );
    }

    public void replaceAdvantages(
            List<String> newAdvantages
    ) {
        if (newAdvantages == null) {
            return;
        }

        this.advantages =
                new ArrayList<>(
                        normalizeStringList(newAdvantages)
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

        return List.of();
    }

    public List<String> getPublicAdvantages() {
        if (advantages == null) {
            return List.of();
        }

        return List.copyOf(advantages);
    }

    public List<String> getPublicSpecialties() {
        if (specialties == null) {
            return List.of();
        }

        return List.copyOf(specialties);
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

    private boolean hasItems(
            List<?> values
    ) {
        return values != null
                && !values.isEmpty();
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }
}