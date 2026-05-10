package com.overduefree.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private int customerTokenDays = 7;
    private int adminTokenHours = 8;
    private String initBossUsername = "boss";
    private String initBossPassword = "boss123456";
}
