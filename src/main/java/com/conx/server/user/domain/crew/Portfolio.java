package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static com.conx.server.global.common.GetOrDefault.getOrDefault;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseEntity {
    private Portfolio(String name, String description, String pdfLink, String thumbnailImageLink, Crew crew){
        this.portfolioName = name;
        this.description = description;
        this.pdfLink = pdfLink;
        this.thumbnailImageLink = thumbnailImageLink;
        this.crew = crew;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String portfolioName;

    private String description;

    private String pdfLink;

    private String thumbnailImageLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    public static Portfolio create(String name,
                                   String description,
                                   String pdfLink,
                                   Crew crew,
                                   String thumbnailImageLink){
        return new Portfolio(name, description, pdfLink, thumbnailImageLink, crew);
    }

    public void modify(ModifyCrewPortfolioRequestDTO req){
        this.portfolioName = getOrDefault(req.name(), this.portfolioName);
        this.description = getOrDefault(req.name(), this.description);
        this.pdfLink = getOrDefault(req.imageLink(), this.pdfLink);
    }
}