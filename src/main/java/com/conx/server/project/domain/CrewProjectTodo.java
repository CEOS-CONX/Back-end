package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.CrewProjectTodoStatus;
import com.conx.server.project.domain.enums.CrewProjectTodoType;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "crew_project_todo",
        indexes = {
                @Index(
                        name = "idx_crew_project_todo_crew_status_created",
                        columnList = "crew_id, status, created_at"
                ),
                @Index(
                        name = "idx_crew_project_todo_project",
                        columnList = "project_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewProjectTodo extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "crew_id",
            nullable = false
    )
    private Crew crew;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private CrewProjectTodoType type;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private CrewProjectTodoStatus status;

    /**
     * 유형명이 나중에 변경되더라도
     * 과거 작업의 이름을 그대로 보존하기 위한 스냅샷
     */
    @Column(
            name = "task_name",
            nullable = false,
            length = 100
    )
    private String taskName;

    private CrewProjectTodo(
            Crew crew,
            Project project,
            CrewProjectTodoType type
    ) {
        this.crew = crew;
        this.project = project;
        this.type = type;
        this.taskName = type.getTaskName();
        this.status =
                CrewProjectTodoStatus.NEEDS_CONFIRMATION;
    }

    public static CrewProjectTodo create(
            Crew crew,
            Project project,
            CrewProjectTodoType type
    ) {
        return new CrewProjectTodo(
                crew,
                project,
                type
        );
    }

    public void markAsInProgress() {
        if (
                this.status
                        == CrewProjectTodoStatus.COMPLETED
        ) {
            return;
        }

        this.status =
                CrewProjectTodoStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status =
                CrewProjectTodoStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.status
                == CrewProjectTodoStatus.COMPLETED;
    }
}