package com.aiems.be.common.storage;

import com.aiems.be.common.config.MinioProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class MinioStorage {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String voiceBucket;
    private final String reportBucket;
    private final Duration presignTtl;

    public MinioStorage(MinioProperties properties) {
        Region region = Region.of(properties.region());
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
        URI endpoint = URI.create(properties.endpoint());

        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentials)
                .endpointOverride(endpoint)
                .forcePathStyle(true)
                .build();
        this.presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentials)
                .endpointOverride(endpoint)
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
        this.voiceBucket = properties.voiceBucket();
        this.reportBucket = properties.reportBucket();
        this.presignTtl = Duration.ofMinutes(properties.presignTtlMinutes());
    }

    public String uploadVoice(Long ambulanceId, byte[] bytes, String contentType, String originalFilename) {
        String key = "voice/%d/%s-%s".formatted(ambulanceId, UUID.randomUUID(), originalFilename);
        s3Client.putObject(
                PutObjectRequest.builder().bucket(voiceBucket).key(key).contentType(contentType).build(),
                RequestBody.fromBytes(bytes));
        return key;
    }

    public String presignReport(String reportKey) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(presignTtl)
                .getObjectRequest(GetObjectRequest.builder().bucket(reportBucket).key(reportKey).build())
                .build();
        return presigner.presignGetObject(presignRequest).url().toString();
    }
}