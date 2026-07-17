package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private CrewLink(
            Crew crew,
            String name,
            String url,
            String description
    ) {
        this.crew = crew;
        this.name = name;
        this.url = url;
        this.description = description;
    }

    public static CrewLink create(
            Crew crew,
            String name,
            String url,
            String description
    ) {
        return new CrewLink(
                crew,
                name,
                url,
                description
        );
    }
}