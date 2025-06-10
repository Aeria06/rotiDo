package com.rotido.backend.controller;

import com.rotido.backend.model.JwtToken;
import com.rotido.backend.model.User;
import com.rotido.backend.repository.JwtTokenRepository;
import com.rotido.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenRepository tokenRepository;

    @GetMapping("/auth-info")
    public ResponseEntity<?> getAuthInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        
        if (auth != null) {
            response.put("authenticated", auth.isAuthenticated());
            response.put("principal", auth.getPrincipal());
            response.put("name", auth.getName());
            response.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            response.put("details", auth.getDetails());
        } else {
            response.put("message", "No authentication found");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("id", user.getId());
        } else {
            response.put("message", "User not found");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tokens")
    public ResponseEntity<?> getAllTokens() {
        List<JwtToken> tokens = tokenRepository.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalTokens", tokens.size());
        response.put("tokens", tokens.stream().map(token -> {
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("id", token.getId());
            tokenInfo.put("username", token.getUsername());
            tokenInfo.put("role", token.getRole());
            tokenInfo.put("expiresAt", token.getExpiresAt());
            tokenInfo.put("tokenPreview", token.getToken().substring(0, Math.min(20, token.getToken().length())) + "...");
            return tokenInfo;
        }).collect(Collectors.toList()));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token/{tokenPreview}")
    public ResponseEntity<?> findTokenByPreview(@PathVariable String tokenPreview) {
        List<JwtToken> allTokens = tokenRepository.findAll();
        
        for (JwtToken token : allTokens) {
            if (token.getToken().startsWith(tokenPreview)) {
                Map<String, Object> response = new HashMap<>();
                response.put("found", true);
                response.put("username", token.getUsername());
                response.put("role", token.getRole());
                response.put("expiresAt", token.getExpiresAt());
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.ok(Map.of("found", false, "message", "Token not found"));
    }

    // This endpoint should be accessible to everyone for testing
    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint - anyone can access");
    }
}