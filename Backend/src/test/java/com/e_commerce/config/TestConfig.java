package com.e_commerce.config;

import com.e_commerce.service.account.RedisService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> mockTemplate = Mockito.mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(mockTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.increment(Mockito.anyString())).thenReturn(1L);
        return mockTemplate;
    }

    @Bean
    @Primary
    public RedisService redisService() {
        RedisService mockRedisService = Mockito.mock(RedisService.class);
        Mockito.when(mockRedisService.increment(Mockito.anyString())).thenReturn(1L);
        Mockito.when(mockRedisService.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(mockRedisService.exists(Mockito.anyString())).thenReturn(false);
        Mockito.when(mockRedisService.getExpire(Mockito.anyString())).thenReturn(Duration.ZERO);
        return mockRedisService;
    }
}
