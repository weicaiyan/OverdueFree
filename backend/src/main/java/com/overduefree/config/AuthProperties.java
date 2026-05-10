package com.overduefree.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private int customerTokenExpireDays = 7;
    private int adminTokenExpireHours = 8;

    public int getCustomerTokenExpireDays() { return customerTokenExpireDays; }
    public void setCustomerTokenExpireDays(int customerTokenExpireDays) { this.customerTokenExpireDays = customerTokenExpireDays; }
    public int getAdminTokenExpireHours() { return adminTokenExpireHours; }
    public void setAdminTokenExpireHours(int adminTokenExpireHours) { this.adminTokenExpireHours = adminTokenExpireHours; }
}
