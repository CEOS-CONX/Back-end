package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByIdAndCrew(long id, Crew crew);

    void deleteByIdAndCrew(long id, Crew crew);
}
