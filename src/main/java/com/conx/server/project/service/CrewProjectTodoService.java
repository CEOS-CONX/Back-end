package com.conx.server.project.service;

import com.conx.server.project.domain.CrewProjectTodo;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.CrewProjectTodoStatus;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.project.repository.CrewProjectTodoRepository;
import com.conx.server.user.domain.crew.Crew;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewProjectTodoService {

    private final CrewProjectTodoRepository
            crewProjectTodoRepository;

    /**
     * 동일 프로젝트·유형의 미완료 Todo가 없을 때만 생성
     */
    @Transactional
    public void createIfAbsent(
            Crew crew,
            Project project,
            CrewProjectTodoType type
    ) {
        boolean exists =
                crewProjectTodoRepository
                        .findFirstByCrewAndProjectAndTypeAndStatusNotOrderByIdDesc(
                                crew,
                                project,
                                type,
                                CrewProjectTodoStatus.COMPLETED
                        )
                        .isPresent();

        if (exists) {
            return;
        }

        CrewProjectTodo todo =
                CrewProjectTodo.create(
                        crew,
                        project,
                        type
                );

        crewProjectTodoRepository.save(todo);
    }

    /**
     * 특정 유형의 가장 최근 미완료 Todo 완료
     */
    @Transactional
    public void completeIfExists(
            Crew crew,
            Project project,
            CrewProjectTodoType type
    ) {
        crewProjectTodoRepository
                .findFirstByCrewAndProjectAndTypeAndStatusNotOrderByIdDesc(
                        crew,
                        project,
                        type,
                        CrewProjectTodoStatus.COMPLETED
                )
                .ifPresent(
                        CrewProjectTodo::complete
                );
    }

    /**
     * 결과물 제출 시 수정 제출 Todo를 우선 완료하고,
     * 수정 Todo가 없으면 최초 제출 Todo를 완료
     */
    @Transactional
    public void completeSubmissionTodo(
            Crew crew,
            Project project
    ) {
        Optional<CrewProjectTodo> revisionTodo =
                crewProjectTodoRepository
                        .findFirstByCrewAndProjectAndTypeAndStatusNotOrderByIdDesc(
                                crew,
                                project,
                                CrewProjectTodoType.REVISION_SUBMISSION,
                                CrewProjectTodoStatus.COMPLETED
                        );

        if (revisionTodo.isPresent()) {
            revisionTodo.get().complete();
            return;
        }

        crewProjectTodoRepository
                .findFirstByCrewAndProjectAndTypeAndStatusNotOrderByIdDesc(
                        crew,
                        project,
                        CrewProjectTodoType.RESULT_SUBMISSION,
                        CrewProjectTodoStatus.COMPLETED
                )
                .ifPresent(
                        CrewProjectTodo::complete
                );
    }
}