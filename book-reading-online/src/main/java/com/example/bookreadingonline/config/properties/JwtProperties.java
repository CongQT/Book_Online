package com.example.bookreadingonline.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "jwt-config")
public class JwtProperties {

  private String secretKey;
  private Duration expiryDuration;
  private Duration refreshDuration;

}
