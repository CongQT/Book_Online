package com.example.bookreadingonline.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "aws-config.s3")
public class AwsS3Properties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;
  private Duration signatureDuration;

}
