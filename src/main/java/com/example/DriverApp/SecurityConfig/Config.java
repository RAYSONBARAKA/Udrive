package com.example.DriverApp.SecurityConfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.DriverApp.Utility.JwtUtil;




@Configuration
@EnableWebSecurity

public class Config {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf((csrf) -> csrf.disable()) // Disable CSRF protection
            .authorizeHttpRequests((authorizeHttpRequests) -> 
                authorizeHttpRequests
                    .requestMatchers(
                    
                    "/api/open/**",
                    "/api/**",
                    "/api/open/students/login/**",
                    "/api/open/uber/**",
                    "/uber/callback/**",
                    "/api/open/ov1/**",
                    "/api/rides/**",
                    
                    "/v2/**",
                        "/v3/**",
                        "/v3/api-docs/**",
                        
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html").permitAll() 
                  
                    .anyRequest().authenticated() 
            )
            .addFilterBefore(new CustomAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement((sessionManagement) -> 
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
           
            .build();
    }

    @Autowired
    JwtUtil jwtUtil;
    
}



