package com.example.DriverApp.Configuration;

import com.cloudinary.Cloudinary;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "ddver1mw9");
        config.put("api_key", "434862595468288");
        config.put("api_secret", "4HNDcqpG0r6fkwJu_MmWBRatpG8");
        return new Cloudinary(config);
    }
}