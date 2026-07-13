package com.conx.server.domain.file.domain;

import com.conx.server.global.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    // 사용자에게 보여줄 원본 파일명
    private String originalName;

    // S3에 저장된 이름(UUID 등)
    private String storedName;

    // 확장자
    private String extension;

    // MIME 타입
    private String contentType;

    // 용량(Byte)
    private Long size;

    // S3 URL 또는 Key
    private String url;

    //설명
    private String explanation;

    // 업로드 시간
    private LocalDateTime createdAt;
}
