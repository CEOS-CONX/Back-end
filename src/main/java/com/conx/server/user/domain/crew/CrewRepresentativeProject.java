package com.conx.server.user.domain.crew;

import com.conx.server.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(
        name = "crew_representative_project",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_crew_representative_project",
                        columnNames = {
                                "crew_id",
                                "project_id"
                        }
                ),
                @UniqueConstraint(
                        name = "uk_crew_representative_order",
                        columnNames = {
                                "crew_id",
                                "display_order"
                        }
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewRepresentativeProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "crew_id",
            nullable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Crew crew;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @Column(
            name = "display_order",
            nullable = false
    )
    private int displayOrder;

    private CrewRepresentativeProject(
            Crew crew,
            Project project,
            int displayOrder
    ) {
        this.crew = crew;
        this.project = project;
        this.displayOrder = displayOrder;
    }

    public static CrewRepresentativeProject create(
            Crew crew,
            Project project,
            int displayOrder
    ) {
        return new CrewRepresentativeProject(
                crew,
                project,
                displayOrder
        );
    }
}