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
    private Portfolio(String name, String fileLink, String imageLink, Crew crew){
        this.portfolioName = name;
        this.fileLink = fileLink;
        this.imageLink = imageLink;
        this.crew = crew;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String portfolioName;

    private String fileLink;

    private String imageLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    public static Portfolio create(String portfolioName,
                                   String fileLink,
                                   String imageLink,
                                   Crew crew){
        return new Portfolio(portfolioName, fileLink, imageLink, crew);
    }

    public static Portfolio create(CrewPortfolioRequestDTO req,
                                   Crew crew){
        return new Portfolio(req.name(), req.fileLink(), req.imageLink(), crew);
    }

    public void modify(ModifyCrewPortfolioRequestDTO req){
        this.portfolioName = getOrDefault(req.name(), this.portfolioName);
        this.fileLink = getOrDefault(req.fileLink(), this.fileLink);
        this.imageLink = getOrDefault(req.imageLink(), this.imageLink);
    }
}