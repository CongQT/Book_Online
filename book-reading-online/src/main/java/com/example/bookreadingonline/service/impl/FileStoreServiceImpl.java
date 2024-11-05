package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.config.properties.AwsS3Properties;
import com.example.bookreadingonline.service.FileStoreService;
import com.example.bookreadingonline.util.AwsS3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileStoreServiceImpl implements FileStoreService {

    private final AwsS3Properties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public String saveFile(String fileKey, String contentType, byte[] content) {
        AwsS3Utils.putObject(
                s3Client,
                properties.getBucket(),
                fileKey,
                contentType,
                content
        );
        return fileKey;
    }

    @Override
    public String getFileUrl(String fileKey) {
        PresignedGetObjectRequest presignedGetObjectRequest = AwsS3Utils.presignGetObject(
                s3Presigner,
                properties.getBucket(),
                fileKey,
                properties.getSignatureDuration()
        );
        return presignedGetObjectRequest.url().toString();
    }
}
