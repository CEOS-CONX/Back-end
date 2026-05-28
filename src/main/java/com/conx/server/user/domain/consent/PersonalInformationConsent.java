package com.conx.server.user.domain.consent;

import com.conx.server.global.BaseEntity;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 개인정보 수집이용 동의
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalInformationConsent extends BaseEntity {
    private PersonalInformationConsent(User user, boolean isAllowed){
        if (user instanceof Crew crew){
            this.crew = crew;
        } else if (user instanceof Company company) {
            this.company = company;
        } else {
            throw new CustomException(ErrorCode.INVALID_USER_TYPE);
        }

        this.isAllowed = isAllowed;
        this.consentedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private boolean isAllowed;

    private LocalDateTime consentedAt;

    public static PersonalInformationConsent create (User user, boolean isAllowed){
        return new PersonalInformationConsent(user, isAllowed);
    }
}
