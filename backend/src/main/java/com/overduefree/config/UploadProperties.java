package com.overduefree.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String baseDir = "C:/OverdueFree/uploads";
    private String publicPrefix = "/uploads";
}
