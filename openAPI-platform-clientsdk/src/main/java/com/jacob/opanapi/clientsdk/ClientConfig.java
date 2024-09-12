package com.jacob.opanapi.clientsdk;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("openapi.client")
@Data
@ComponentScan
public class ClientConfig {
    private String accessKey;
    private String secretKey;

}
