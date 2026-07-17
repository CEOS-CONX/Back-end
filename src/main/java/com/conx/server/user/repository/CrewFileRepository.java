package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.CrewFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewFileRepository
        extends JpaRepository<CrewFile, Long> {

    List<CrewFile> findAllByCrewIdOrderByIdAsc(
            Long crewId
    );

    void deleteAllByCrewId(Long crewId);
}