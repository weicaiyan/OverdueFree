package com.overduefree.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String baseDir = "C:/OverdueFree/uploads";
    private String publicPrefix = "/uploads";

    public String getBaseDir() { return baseDir; }
    public void setBaseDir(String baseDir) { this.baseDir = baseDir; }
    public String getPublicPrefix() { return publicPrefix; }
    public void setPublicPrefix(String publicPrefix) { this.publicPrefix = publicPrefix; }
}
