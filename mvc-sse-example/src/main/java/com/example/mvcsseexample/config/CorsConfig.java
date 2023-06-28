package com.example.mvcsseexample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Specify the allowed origin(s)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Specify the allowed HTTP methods
                .allowedHeaders("*") // Specify the allowed headers
                .allowCredentials(true) // Allow sending of credentials (e.g., cookies)
                .maxAge(3600); // Cache preflight response for 1 hour (3600 seconds)
    }
}
