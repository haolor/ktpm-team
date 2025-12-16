package com.e_commerce.service.account.token;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Boolean> blacklistedTokens = new ConcurrentHashMap<>();
    public void addToBlacklist(String token) {
        blacklistedTokens.put(token, true);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
}