package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.CrewLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewLinkRepository
        extends JpaRepository<CrewLink, Long> {

    List<CrewLink> findAllByCrewIdOrderByIdAsc(
            Long crewId
    );

    void deleteAllByCrewId(Long crewId);
}