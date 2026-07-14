package com.conx.server.domain.file.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {
    private File(String storedName, String extension, String contentType,
                 Long size, String url, String explanation){
        this.storedName = storedName;
        this.extension = extension;
        this.contentType = contentType;
        this.size = size;
        this.url = url;
        this.explanation = explanation;
        this.createdAt = LocalDateTime.now();
    }
    @Id
    @GeneratedValue
    private Long id;

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

    public static File create(HeadObjectResponse h, String url, String explanation){
        URI uri = URI.create(url);
        String storedName = uri.getPath().substring(1);

        String fileName = Paths.get(storedName).getFileName().toString();

        String extension = "";
        int dot = fileName.lastIndexOf('.');
        if (dot != -1) {
            extension = fileName.substring(dot + 1);
        }

        return new File(
            storedName, extension, h.contentType(), h.contentLength(), url, explanation
        );
    }
}
