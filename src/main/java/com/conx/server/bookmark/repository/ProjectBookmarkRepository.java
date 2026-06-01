package com.conx.server.bookmark.repository;

import com.conx.server.bookmark.domain.ProjectBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long> {

    Page<ProjectBookmark> findAllByCrewId(Long crewId, Pageable pageable);

    boolean existsByCrewIdAndProjectId(Long crewId, Long projectId);

    Optional<ProjectBookmark> findByCrewIdAndProjectId(Long crewId, Long projectId);

    void deleteByCrewIdAndProjectId(Long crewId, Long projectId);
}