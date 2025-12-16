package com.e_commerce.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.util.JwtUtil;

import static org.mockito.Mockito.mock;

/**
 * Test configuration that provides mock beans for JWT-related dependencies.
 * This prevents JwtAuthenticationFilter from failing to autowire in WebMvcTest contexts.
 */
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return mock(JwtUtil.class);
    }

    @Bean
    @Primary
    public TokenBlacklistService tokenBlacklistService() {
        return mock(TokenBlacklistService.class);
    }

    @Bean
    @Primary
    public AccountService accountService() {
        return mock(AccountService.class);
    }
}
