package com.conx.server.user.repository;

import com.conx.server.user.domain.consent.PersonalInformationConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalInformationConsentRepository extends JpaRepository<
        PersonalInformationConsent, Long> {
}
