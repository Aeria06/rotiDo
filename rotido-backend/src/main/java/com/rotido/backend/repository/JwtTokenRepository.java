package com.rotido.backend.repository;

import com.rotido.backend.model.JwtToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends MongoRepository<JwtToken, String> {
    Optional<JwtToken> findByToken(String token);
    
    // Delete tokens by username (for cleanup during new login)
    void deleteByUsername(String username);
    
    // Delete specific token (for logout)
    void deleteByToken(String token);
    
    // Find by username
    List<JwtToken> findByUsername(String username);
    
    // Find expired tokens for cleanup
    @Query("{'expiresAt': {$lt: ?0}}")
    List<JwtToken> findExpiredTokens(Date currentTime);
    
    // Delete expired tokens
    @Query(value = "{'expiresAt': {$lt: ?0}}", delete = true)
    void deleteExpiredTokens(Date currentTime);
}