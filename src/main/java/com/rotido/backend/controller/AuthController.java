package com.rotido.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotido.backend.model.JwtToken;
import com.rotido.backend.model.User;
import com.rotido.backend.repository.JwtTokenRepository;
import com.rotido.backend.repository.UserRepository;
import com.rotido.backend.security.JwtUtil;
import com.rotido.backend.services.CryptoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepo;
    @Autowired private JwtTokenRepository tokenRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CryptoService cryptoService;

    @Value("${app.encryption.key:MyCompanySecretKey2024!}")
    private String companyKey;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/log")
    @Transactional
    public ResponseEntity<?> loginEncrypted(@RequestBody Map<String, String> payload) {
        try {
            String encrypted = payload.get("encrypted");
            if (encrypted == null) {
                String error = cryptoService.encrypt("{\"error\":\"Missing encrypted payload\"}");
                return ResponseEntity.badRequest().body(Map.of("encrypted", error));
            }

            String decryptedJson = cryptoService.decrypt(encrypted);
            Map<String, String> loginData = objectMapper.readValue(
                decryptedJson, new TypeReference<Map<String, String>>() {}
            );

            String username = loginData.get("username");
            String password = loginData.get("password");

            User user = userRepo.findByUsername(username).orElse(null);
            if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                String error = cryptoService.encrypt("{\"error\":\"Invalid credentials\"}");
                return ResponseEntity.status(401).body(Map.of("encrypted", error));
            }

            tokenRepo.deleteByUsername(username);

            long expirationMillis = 10 * 60 * 1000;
            Date expiration = new Date(System.currentTimeMillis() + expirationMillis);
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), expiration);

            JwtToken jwtToken = new JwtToken();
            jwtToken.setToken(token);
            jwtToken.setUsername(user.getUsername());
            jwtToken.setRole(user.getRole());
            jwtToken.setExpiresAt(expiration);
            tokenRepo.save(jwtToken);

            // ✅ Add redirectTo based on role
            String redirectTo;
            switch (user.getRole()) {
                case "admin" -> redirectTo = "/dashboard/admin";
                case "customer" -> redirectTo = "/dashboard/customer";
                case "service" -> redirectTo = "/dashboard/service";
                default -> redirectTo = "/dashboard/profile";
            }

            Map<String, Object> response = Map.of(
                "token", token,
                "role", user.getRole(),
                "expiresAt", expiration,
                "message", "Login successful",
                "redirectTo", redirectTo
            );

            String encryptedResponse = cryptoService.encrypt(objectMapper.writeValueAsString(response));
            return ResponseEntity.ok(Map.of("encrypted", encryptedResponse));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> loginCombined(@RequestBody Map<String, String> credentials) {
        try {
            if (credentials == null || credentials.get("username") == null || credentials.get("password") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }

            String plaintext = objectMapper.writeValueAsString(credentials);
            String encrypted = cryptoService.encrypt(plaintext);
            Map<String, String> encryptedPayload = Map.of("encrypted", encrypted);

            ResponseEntity<?> encryptedResponse = loginEncrypted(encryptedPayload);

            if (encryptedResponse.getStatusCode().is2xxSuccessful()) {
                Map<?, ?> body = (Map<?, ?>) encryptedResponse.getBody();
                String encryptedResult = (String) body.get("encrypted");
                String decryptedResult = cryptoService.decrypt(encryptedResult);
                return ResponseEntity.ok(objectMapper.readValue(decryptedResult, Map.class));
            } else {
                Map<?, ?> body = (Map<?, ?>) encryptedResponse.getBody();
                String encryptedError = (String) body.get("encrypted");
                if (encryptedError == null) {
                    Object errorMsg = body.get("error");
                    return ResponseEntity.status(encryptedResponse.getStatusCode())
                        .body(Map.of("error", errorMsg != null ? errorMsg.toString() : "Unknown login error"));
                }
                String decryptedError = cryptoService.decrypt(encryptedError);
                return ResponseEntity.status(encryptedResponse.getStatusCode())
                        .body(objectMapper.readValue(decryptedError, Map.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenRepo.deleteByToken(token);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
