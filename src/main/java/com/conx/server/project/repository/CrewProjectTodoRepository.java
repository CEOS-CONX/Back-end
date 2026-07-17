package com.conx.server.project.repository;

import com.conx.server.project.domain.CrewProjectTodo;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.CrewProjectTodoStatus;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.user.domain.crew.Crew;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewProjectTodoRepository
        extends JpaRepository<CrewProjectTodo, Long> {

    Optional<CrewProjectTodo> findByIdAndCrewId(
            Long todoId,
            Long crewId
    );

    Page<CrewProjectTodo> findAllByCrewId(
            Long crewId,
            Pageable pageable
    );

    List<CrewProjectTodo>
    findAllByCrewAndProjectAndStatusNot(
            Crew crew,
            Project project,
            CrewProjectTodoStatus status
    );

    Optional<CrewProjectTodo>
    findFirstByCrewAndProjectAndTypeAndStatusNotOrderByIdDesc(
            Crew crew,
            Project project,
            CrewProjectTodoType type,
            CrewProjectTodoStatus status
    );

    /**
     * 상태 필터 없이 Todo 조회
     */
    @Query(
            value = """
                    select todo
                    from CrewProjectTodo todo
                    join fetch todo.project project
                    where todo.crew.id = :crewId
                      and (
                            :keyword is null
                            or todo.taskName like concat('%', :keyword, '%')
                            or project.projectName like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                      )
                    """,
            countQuery = """
                    select count(todo)
                    from CrewProjectTodo todo
                    join todo.project project
                    where todo.crew.id = :crewId
                      and (
                            :keyword is null
                            or todo.taskName like concat('%', :keyword, '%')
                            or project.projectName like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                      )
                    """
    )
    Page<CrewProjectTodo> findCrewTodoProjects(
            @Param("crewId")
            Long crewId,

            @Param("keyword")
            String keyword,

            Pageable pageable
    );

    /**
     * 상태 필터를 포함한 Todo 조회
     */
    @Query(
            value = """
                    select todo
                    from CrewProjectTodo todo
                    join fetch todo.project project
                    where todo.crew.id = :crewId
                      and todo.status = :progressStatus
                      and (
                            :keyword is null
                            or todo.taskName like concat('%', :keyword, '%')
                            or project.projectName like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                      )
                    """,
            countQuery = """
                    select count(todo)
                    from CrewProjectTodo todo
                    join todo.project project
                    where todo.crew.id = :crewId
                      and todo.status = :progressStatus
                      and (
                            :keyword is null
                            or todo.taskName like concat('%', :keyword, '%')
                            or project.projectName like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                      )
                    """
    )
    Page<CrewProjectTodo> findCrewTodoProjectsByStatus(
            @Param("crewId")
            Long crewId,

            @Param("keyword")
            String keyword,

            @Param("progressStatus")
            CrewProjectTodoStatus progressStatus,

            Pageable pageable
    );

    /**
     * 대시보드에 표시할 최신 미완료 Todo 조회
     */
    @Query("""
        select todo
        from CrewProjectTodo todo
        join fetch todo.project project
        where todo.crew.id = :crewId
          and todo.status <> :completedStatus
        order by todo.createdAt desc, todo.id desc
        """)
    List<CrewProjectTodo> findDashboardTodos(
            @Param("crewId")
            Long crewId,

            @Param("completedStatus")
            CrewProjectTodoStatus completedStatus,

            Pageable pageable
    );
}