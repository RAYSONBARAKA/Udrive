package com.example.DriverApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.cloudinary.Cloudinary;
import com.example.DriverApp.Utility.JwtUtil;

@SpringBootApplication
@EnableScheduling 
public class DriverAppApplication implements CommandLineRunner {

    @Autowired
    private Cloudinary cloudinary;

    public static void main(String[] args) {
        SpringApplication.run(DriverAppApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Override
    public void run(String... args) {
        if (cloudinary != null) {
            System.out.println("Cloudinary bean initialized successfully!");
        } else {
            System.out.println("Cloudinary bean is null.");
        }
    }
}
