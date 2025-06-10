package com.rotido.backend.services;


import com.rotido.backend.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenCleanupService {

    @Autowired
    private JwtTokenRepository tokenRepository;

    // Run every 2 minutes to clean up expired tokens
    @Scheduled(fixedRate = 120000) // 2 minutes
    public void cleanupExpiredTokens() {
        Date now = new Date();
        tokenRepository.deleteExpiredTokens(now);
        System.out.println("Cleaned up expired tokens at: " + now);
    }
}