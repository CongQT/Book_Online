package com.example.bookreadingonline.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mail-config")
public class MailProperties {

    private String senderName;
    private String senderAddress;

}
