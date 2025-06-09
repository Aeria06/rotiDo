package com.rotido.backend.controller;

import com.rotido.backend.model.User;
import com.rotido.backend.repository.UserRepository;
import com.rotido.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return "User registered";
    }

    // @PostMapping("/login")
    // public String login(@RequestBody User user) {
    //     authManager.authenticate(
    //         new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
    //     );
    //     User dbUser = repo.findByUsername(user.getUsername()).get();
    //     return jwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole());
    // }
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    try {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
    } catch (BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    User dbUser = repo.findByUsername(user.getUsername())
                      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String token = jwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole());

    return ResponseEntity.ok(token);
}

}
