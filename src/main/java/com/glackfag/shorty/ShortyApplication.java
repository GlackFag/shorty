package com.glackfag.shorty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@SpringBootApplication
@EnableWebMvc
@EnableJpaRepositories(basePackages = "com.glackfag.shorty.repositories.jpa")
@EnableRedisRepositories(basePackages = "com.glackfag.shorty.repositories.redis")
@Configuration
public class ShortyApplication {
    private final Environment environment;

    public ShortyApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(ShortyApplication.class, args);
    }

    @Bean
    public String botUrl(){
        return environment.getRequiredProperty("bot.url");
    }

    @Bean
    public List<String> reservedPaths() {
        return List.of("stats", "api", "bot", "report");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}

