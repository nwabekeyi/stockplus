package com.stockmgmt.api.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            org.springframework.core.env.Environment env) {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", env.getRequiredProperty("cloudinary.cloud-name"));
        config.put("api_key", env.getRequiredProperty("cloudinary.api-key"));
        config.put("api_secret", env.getRequiredProperty("cloudinary.api-secret"));
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
