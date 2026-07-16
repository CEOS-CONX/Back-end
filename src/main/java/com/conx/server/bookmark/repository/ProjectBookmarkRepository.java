package com.conx.server.bookmark.repository;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.crew.Crew;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long> {

    Page<ProjectBookmark> findAllByCrewId(Long crewId, Pageable pageable);

    boolean existsByCrewIdAndProjectId(Long crewId, Long projectId);

    Optional<ProjectBookmark> findByCrewIdAndProjectId(Long crewId, Long projectId);

    @Query("""
        select pm from ProjectBookmark pm
        where pm.project.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and pm.project.projectDeadline in :deadlines
    """)
    List<ProjectBookmark> findAllAboutDeadline(
            @Param("deadlines") List<LocalDate> deadlines
    );

    boolean existsByCrewAndProject(Crew crew, Project project);
}