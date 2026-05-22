package com.conx.server.domain.file.service;

import com.conx.server.domain.file.dto.PresignedUrlRequest;
import com.conx.server.domain.file.dto.PresignedUrlResponse;
import com.conx.server.global.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    @Value("${cloud.aws.region}")
    private String region;

    public PresignedUrlResponse createPresignedUrl(PresignedUrlRequest request) {
        String fileKey = createFileKey(request.getFileName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(fileKey)
                .contentType(request.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();

        String fileUrl = createFileUrl(fileKey);

        return PresignedUrlResponse.of(
                presignedUrl.toString(),
                fileUrl,
                fileKey
        );
    }

    private String createFileKey(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        return "uploads/" + uuid + "-" + originalFileName;
    }

    private String createFileUrl(String fileKey) {
        return "https://" + s3Properties.getBucket()
                + ".s3." + region + ".amazonaws.com/"
                + fileKey;
    }
}