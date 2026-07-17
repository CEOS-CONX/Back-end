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

import java.util.Locale;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String extension;

    private long size;

    private String url;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private CrewFile(
            Crew crew,
            String fileName,
            String extension,
            long size,
            String url,
            String description
    ) {
        this.crew = crew;
        this.fileName = fileName;
        this.extension = resolveExtension(
                fileName,
                extension
        );
        this.size = size;
        this.url = url;
        this.description = description;
    }

    public static CrewFile create(
            Crew crew,
            String fileName,
            String extension,
            Long size,
            String url,
            String description
    ) {
        return new CrewFile(
                crew,
                fileName,
                extension,
                size == null ? 0L : size,
                url,
                description
        );
    }

    private static String resolveExtension(
            String fileName,
            String extension
    ) {
        if (extension != null && !extension.isBlank()) {
            String normalized = extension.trim();

            if (normalized.startsWith(".")) {
                normalized = normalized.substring(1);
            }

            return normalized.toLowerCase(Locale.ROOT);
        }

        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        int lastDotIndex = fileName.lastIndexOf('.');

        if (
                lastDotIndex < 0
                        || lastDotIndex == fileName.length() - 1
        ) {
            return null;
        }

        return fileName
                .substring(lastDotIndex + 1)
                .toLowerCase(Locale.ROOT);
    }
}