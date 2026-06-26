package com.conx.server.domain.file.service;

import com.conx.server.domain.file.dto.PresignedUrlRequest;
import com.conx.server.domain.file.dto.PresignedUrlResponse;
import com.conx.server.global.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final S3Client s3Client;

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

    public InputStream download(String fileKey) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(fileKey)
                .build();

        return s3Client.getObject(request);
    }

    public String upload(
            byte[] image,
            String key
    ) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .contentType("image/png")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(image)
        );

        return createFileUrl(key);
    }

    public void delete(String key) {

        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(s3Properties.getBucket())
                        .key(key)
                        .build();

        s3Client.deleteObject(request);
    }
}