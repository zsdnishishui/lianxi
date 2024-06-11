package com.lianxi.auth.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.TokenService;

import java.security.SecureRandom;

@Configuration
public class CommentConfig {
    @Bean
    public TokenService tokenService()  {
        KeyBasedPersistenceTokenService tokenService = new KeyBasedPersistenceTokenService();
        tokenService.setServerSecret("121212");
        tokenService.setServerInteger(1);
        tokenService.setSecureRandom(new SecureRandom());
        return tokenService;
    }
}
