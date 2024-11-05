package com.example.bookreadingonline.config;

import com.example.bookreadingonline.config.properties.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AwsS3Properties.class)
public class AwsS3Config {

  private final AwsS3Properties properties;

  @Bean
  public AwsCredentials awsCredentials() {
    return AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey());
  }

  @Bean
  public S3Client s3Client(AwsCredentials awsCredentials) {
    return S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .region(Region.of(properties.getRegion()))
        .build();
  }

  @Bean
  public S3Presigner s3Presigner(AwsCredentials awsCredentials) {
    return S3Presigner.builder()
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .region(Region.of(properties.getRegion()))
        .build();
  }

}
