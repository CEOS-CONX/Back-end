package com.conx.server.user.repository;

import com.conx.server.user.domain.consent.PromotionalMessageConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionalMessageConsentRepository extends JpaRepository<
        PromotionalMessageConsent, Long> {
}
