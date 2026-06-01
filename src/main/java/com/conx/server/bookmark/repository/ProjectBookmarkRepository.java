package com.conx.server.bookmark.repository;

import com.conx.server.bookmark.domain.ProjectBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long> {

    Page<ProjectBookmark> findAllByCrewId(Long crewId, Pageable pageable);
}