package com.conx.server.global.common;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.service.CrewProjectTodoService;
import com.conx.server.user.domain.crew.Crew;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectWorkSchedulerTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CrewProjectTodoService crewProjectTodoService;

    @Mock
    private Project project;

    @Mock
    private Crew crew;

    @InjectMocks
    private ProjectWorkScheduler projectWorkScheduler;

    @Test
    @DisplayName("프로젝트 마감일이 되면 결과물 제출 대기로 변경하고 Todo를 생성한다")
    void endOfSubmitDateCreatesResultSubmissionTodo() {
        // given
        given(
                projectRepository
                        .findAllByProjectDeadlineAndStatus(
                                any(LocalDate.class),
                                eq(ProjectStatus.PROGRESS)
                        )
        ).willReturn(
                List.of(project)
        );

        given(project.getSelectedCrew())
                .willReturn(crew);

        // when
        projectWorkScheduler.endOfSubmitDate();

        // then
        verify(project)
                .afterProjectDeadline();

        verify(crewProjectTodoService)
                .createIfAbsent(
                        crew,
                        project,
                        CrewProjectTodoType.RESULT_SUBMISSION
                );
    }

    @Test
    @DisplayName("선정된 크루가 없으면 결과물 제출 Todo를 생성하지 않는다")
    void endOfSubmitDateWithoutSelectedCrew() {
        // given
        given(
                projectRepository
                        .findAllByProjectDeadlineAndStatus(
                                any(LocalDate.class),
                                eq(ProjectStatus.PROGRESS)
                        )
        ).willReturn(
                List.of(project)
        );

        given(project.getSelectedCrew())
                .willReturn(null);

        // when
        projectWorkScheduler.endOfSubmitDate();

        // then
        verify(project)
                .afterProjectDeadline();

        verify(
                crewProjectTodoService,
                never()
        ).createIfAbsent(
                any(Crew.class),
                any(Project.class),
                any(CrewProjectTodoType.class)
        );
    }

    @Test
    @DisplayName("모집 기간이 만료된 프로젝트를 만료 처리한다")
    void expireProject() {
        // given
        given(
                projectRepository.findExpireProject(
                        any(LocalDate.class)
                )
        ).willReturn(
                List.of(project)
        );

        // when
        projectWorkScheduler.expireProject();

        // then
        verify(project)
                .expire();
    }
}