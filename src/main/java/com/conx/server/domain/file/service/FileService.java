package com.conx.server.domain.file.service;

import com.conx.server.domain.file.dto.PresignedUrlRequest;
import com.conx.server.domain.file.dto.PresignedUrlResponse;
import com.conx.server.global.config.S3Properties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @PostConstruct // 또는 테스트 코드에서
    public void checkCredentials() {
        try {
            s3Client.listBuckets();
            System.out.println("자격증명 OK - S3 접근 가능");
        } catch (Exception e) {
            System.out.println("자격증명 문제: " + e.getMessage());
        }
    }

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

    //TODO: AWS 자격증명
    /*
    public HeadObjectResponse getHeadObject(String presignedUrl){
        String key = extractKey(presignedUrl);

        return s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(s3Properties.getBucket())
                        .key(key)
                        .build()
        );
    }

    private String extractKey(String presignedUrl) {
        URI uri = URI.create(presignedUrl);
        String path = uri.getPath(); // 쿼리스트링(서명 등)은 자동으로 분리됨
        return path.startsWith("/") ? path.substring(1) : path;
    }
     */
}