package com.innowise.innowiseuserservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return objectMapper;
  }

  @Bean
  public GenericJackson2JsonRedisSerializer redisSerializer() {
    ObjectMapper redisMapper = new ObjectMapper();
    redisMapper.registerModule(new JavaTimeModule());
    redisMapper.activateDefaultTyping(
        redisMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL
    );
    return new GenericJackson2JsonRedisSerializer(redisMapper);
  }

  @Bean
  public RedisCacheConfiguration cacheConfiguration(GenericJackson2JsonRedisSerializer redisSerializer) {

    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(24))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer)
        );
  }

  @Bean
  @Primary
  public RedisCacheManager cacheManager(
      RedisConnectionFactory connectionFactory,
      GenericJackson2JsonRedisSerializer redisSerializer) {

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(cacheConfiguration(redisSerializer))
        .build();
  }

}
