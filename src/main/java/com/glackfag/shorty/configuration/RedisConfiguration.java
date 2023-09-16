package com.glackfag.shorty.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glackfag.shorty.models.Association;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.username}")
    private String redisUsername;

    @Value("${spring.redis.password}")
    private String redisPassword;

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        configuration.setUsername(redisUsername);
        configuration.setPassword(redisPassword);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Association> redisTemplate() {
        RedisTemplate<String, Association> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        Jackson2JsonRedisSerializer<Association> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper ,Association.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(valueSerializer);

        return template;
    }
}
