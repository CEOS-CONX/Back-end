package com.conx.server.global.common;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.service.CrewProjectTodoService;
import com.conx.server.user.domain.crew.Crew;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectWorkScheduler {

    private final ProjectRepository projectRepository;
    private final CrewProjectTodoService crewProjectTodoService;

    /**
     * 모집 마감일이 지난 프로젝트 만료 처리
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void expireProject() {
        List<Project> expiredProjects =
                projectRepository.findExpireProject(
                        LocalDate.now()
                );

        expiredProjects.forEach(
                Project::expire
        );
    }

    /**
     * 프로젝트 수행 마감일 도달 시
     * 결과물 제출 대기 상태로 전환하고
     * 크루에게 결과물 제출 Todo를 생성
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void endOfSubmitDate() {
        List<Project> projects =
                projectRepository
                        .findAllByProjectDeadlineAndStatus(
                                LocalDate.now(),
                                ProjectStatus.PROGRESS
                        );

        for (Project project : projects) {
            project.afterProjectDeadline();

            Crew selectedCrew =
                    project.getSelectedCrew();

            if (selectedCrew == null) {
                continue;
            }

            crewProjectTodoService.createIfAbsent(
                    selectedCrew,
                    project,
                    CrewProjectTodoType.RESULT_SUBMISSION
            );
        }
    }
}