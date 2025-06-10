// package com.rotido.backend.controller;

// import com.rotido.backend.model.User;
// import com.rotido.backend.repository.UserRepository;
// import com.rotido.backend.security.JwtUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.*;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/auth")
// public class AuthController {

//     @Autowired
//     private UserRepository repo;

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Autowired
//     private AuthenticationManager authManager;

//     @Autowired
//     private PasswordEncoder encoder;

//     @PostMapping("/register")
//     public String register(@RequestBody User user) {
//         user.setPassword(encoder.encode(user.getPassword()));
//         repo.save(user);
//         return "User registered";
//     }

//     // @PostMapping("/login")
//     // public String login(@RequestBody User user) {
//     //     authManager.authenticate(
//     //         new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
//     //     );
//     //     User dbUser = repo.findByUsername(user.getUsername()).get();
//     //     return jwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole());
//     // }
//     @PostMapping("/login")
// public ResponseEntity<?> login(@RequestBody User user) {
//     try {
//         authManager.authenticate(
//             new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
//         );
//     } catch (BadCredentialsException ex) {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//     }

//     User dbUser = repo.findByUsername(user.getUsername())
//                       .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//     String token = jwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole());

//     return ResponseEntity.ok(token);
// }

// }

//chatgpt
// package com.rotido.backend.controller;

// import org.springframework.web.bind.annotation.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;

// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;

// import com.rotido.backend.model.JwtToken;
// import com.rotido.backend.model.User;
// import com.rotido.backend.repository.JwtTokenRepository;
// import com.rotido.backend.repository.UserRepository;
// import com.rotido.backend.security.JwtUtil;
// import org.springframework.security.crypto.password.PasswordEncoder;

// @RestController
// @RequestMapping("/auth")
// public class AuthController {

//     @Autowired private JwtUtil jwtUtil;
//     @Autowired private UserRepository userRepo;
//     @Autowired private JwtTokenRepository tokenRepo;
//     @Autowired private PasswordEncoder passwordEncoder;

//     // 👑 Only ADMINs can access this due to SecurityConfig
//     @PostMapping("/register")
//     public ResponseEntity<String> register(@RequestBody User user) {
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         userRepo.save(user);
//         return ResponseEntity.ok("User registered successfully");
//     }

//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
//         String username = payload.get("username");
//         String password = payload.get("password");

//         User user = userRepo.findByUsername(username).orElse(null);
//         if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
//             return ResponseEntity.status(401).body("Invalid credentials");
//         }

//         // ✅ Set expiration time in sync (e.g. 10 seconds)
//         long expirationMillis =60_000; // 10 seconds
//         Date expiration = new Date(System.currentTimeMillis() + expirationMillis);

//         // ✅ Generate token with synced expiration
//         String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), expiration);

//         JwtToken jwtToken = new JwtToken();
//         jwtToken.setToken(token);
//         jwtToken.setUsername(user.getUsername());
//         jwtToken.setExpiresAt(expiration);
//         tokenRepo.save(jwtToken);

//         Map<String, Object> response = new HashMap<>();
//         response.put("token", token);
//         response.put("expiresAt", expiration);

//         return ResponseEntity.ok(response);
//     }
// }
package com.rotido.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rotido.backend.model.JwtToken;
import com.rotido.backend.model.User;
import com.rotido.backend.repository.JwtTokenRepository;
import com.rotido.backend.repository.UserRepository;
import com.rotido.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepo;
    @Autowired private JwtTokenRepository tokenRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // 👑 Only ADMINs can access this due to SecurityConfig
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");

            System.out.println("Login attempt for username: " + username);

            User user = userRepo.findByUsername(username).orElse(null);
            if (user == null) {
                System.out.println("User not found: " + username);
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("Password mismatch for user: " + username);
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            System.out.println("User authenticated: " + username + ", Role: " + user.getRole());

            // Clean up any existing tokens for this user
            try {
                tokenRepo.deleteByUsername(username);
                System.out.println("Cleaned up existing tokens for user: " + username);
            } catch (Exception e) {
                System.out.println("Error cleaning up tokens: " + e.getMessage());
            }
            
            // Set expiration time to 1 minute (60,000 milliseconds)
            long expirationMillis = 3*60*1000;
            Date expiration = new Date(System.currentTimeMillis() + expirationMillis);

            // Generate token with role-specific claims
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), expiration);
            System.out.println("Generated token: " + token.substring(0, 20) + "...");

            // Save token to database with detailed logging
            try {
                JwtToken jwtToken = new JwtToken();
                jwtToken.setToken(token);
                jwtToken.setUsername(user.getUsername());
                jwtToken.setRole(user.getRole());
                jwtToken.setExpiresAt(expiration);
                
                JwtToken savedToken = tokenRepo.save(jwtToken);
                System.out.println("Token saved successfully with ID: " + savedToken.getId());
                
                // Verify token was saved
                if (tokenRepo.findByToken(token).isPresent()) {
                    System.out.println("Token verification successful - found in database");
                } else {
                    System.out.println("ERROR: Token not found in database after saving!");
                }
                
            } catch (Exception e) {
                System.out.println("Error saving token: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error creating session");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("expiresAt", expiration);
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Delete the token from database to invalidate it
            tokenRepo.deleteByToken(token);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}   