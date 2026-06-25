package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectQuestionRepository extends JpaRepository<ProjectQuestion, Long> {

    Page<ProjectQuestion> findAllByProjectIdOrderByIdDesc(Long projectId, Pageable pageable);

    Optional<ProjectQuestion> findByIdAndProjectId(Long questionId, Long projectId);
}
