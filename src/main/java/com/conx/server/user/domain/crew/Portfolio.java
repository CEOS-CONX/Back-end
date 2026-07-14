package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
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

import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String portfolioName;

    private String description;

    /*
     * 실제 포트폴리오 PDF 또는 파일 URL입니다.
     */
    private String pdfLink;

    /*
     * 포트폴리오 썸네일 이미지 URL입니다.
     */
    private String thumbnailImageLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "crew_id",
            nullable = false
    )
    private Crew crew;

    private Portfolio(
            Crew crew,
            String name,
            String description,
            String fileUrl,
            String imageUrl
    ) {
        this.crew = crew;
        this.portfolioName = name;
        this.description = description;
        this.pdfLink = fileUrl;
        this.thumbnailImageLink = imageUrl;
    }

    public static Portfolio create(
            Crew crew,
            String name,
            String description,
            String fileUrl,
            String imageUrl
    ) {
        return new Portfolio(
                crew,
                name,
                description,
                fileUrl,
                imageUrl
        );
    }

    public void modify(
            ModifyCrewPortfolioRequestDTO request
    ) {
        this.portfolioName =
                getOrDefault(
                        request.name(),
                        this.portfolioName
                );

        this.description =
                getOrDefault(
                        request.description(),
                        this.description
                );

        this.pdfLink =
                getOrDefault(
                        request.fileUrl(),
                        this.pdfLink
                );

        this.thumbnailImageLink =
                getOrDefault(
                        request.imageUrl(),
                        this.thumbnailImageLink
                );
    }
}