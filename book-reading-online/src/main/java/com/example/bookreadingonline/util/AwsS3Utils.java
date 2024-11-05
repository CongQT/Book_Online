package com.example.bookreadingonline.util;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

public class AwsS3Utils {

  public static PresignedGetObjectRequest presignGetObject(
      S3Presigner s3Presigner,
      String bucket,
      String key,
      Duration signatureDuration
  ) {

    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(r -> r
            .bucket(bucket)
            .key(key))
        .signatureDuration(signatureDuration)
        .build();

    return s3Presigner.presignGetObject(getObjectPresignRequest);

  }

  public static PresignedPutObjectRequest presignPutObject(
      S3Presigner s3Presigner,
      String bucket,
      String key,
      String contentType,
      ObjectCannedACL acl,
      Duration signatureDuration
  ) {

    PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
        .putObjectRequest(r -> r
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .acl(acl))
        .signatureDuration(signatureDuration)
        .build();

    return s3Presigner.presignPutObject(putObjectPresignRequest);

  }

  public static PutObjectResponse putObject(
      S3Client s3Client,
      String bucket,
      String key,
      String contentType,
      byte[] content
  ) {

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(contentType)
        .build();

    return s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

  }

}
