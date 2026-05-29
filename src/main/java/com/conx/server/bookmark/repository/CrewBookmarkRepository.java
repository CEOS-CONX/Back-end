package com.conx.server.bookmark.repository;

import com.conx.server.bookmark.domain.CrewBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrewBookmarkRepository extends JpaRepository<CrewBookmark, Long> {

    Optional<CrewBookmark> findByCompanyIdAndCrewId(Long companyId, Long crewId);

    List<CrewBookmark> findAllByCompanyId(Long companyId);
}