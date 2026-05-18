package com.overduefree.config;

import com.overduefree.auth.AdminAuthInterceptor;
import com.overduefree.auth.CustomerAuthInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({UploadProperties.class, AuthProperties.class})
public class WebConfig implements WebMvcConfigurer {

    private static final String[] ALLOWED_ORIGIN_PATTERNS = {
        "http://localhost:[*]",
        "http://127.0.0.1:[*]",
        "http://192.168.*.*:[*]",
        "http://10.*.*.*:[*]",
        "http://172.*.*.*:[*]"
    };

    private final UploadProperties uploadProperties;
    private final CustomerAuthInterceptor customerAuthInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    public WebConfig(UploadProperties uploadProperties,
                     CustomerAuthInterceptor customerAuthInterceptor,
                     AdminAuthInterceptor adminAuthInterceptor) {
        this.uploadProperties = uploadProperties;
        this.customerAuthInterceptor = customerAuthInterceptor;
        this.adminAuthInterceptor = adminAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS)
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Content-Disposition")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerAuthInterceptor)
            .addPathPatterns("/api/app/**")
            .excludePathPatterns("/api/app/auth/send-code", "/api/app/auth/login");
        registry.addInterceptor(adminAuthInterceptor)
            .addPathPatterns("/api/admin/**")
            .excludePathPatterns("/api/admin/auth/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPrefix = uploadProperties.getPublicPrefix();
        String baseDir = uploadProperties.getBaseDir().replace("\\", "/");
        registry.addResourceHandler(publicPrefix + "/**")
            .addResourceLocations("file:" + baseDir + "/");
    }
}
