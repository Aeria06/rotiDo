// package com.rotido.backend.filter;

// import com.rotido.backend.repository.JwtTokenRepository;
// import com.rotido.backend.security.JwtUtil;
// import io.jsonwebtoken.Claims;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.util.Collections;

// @Component
// public class JwtRequestFilter extends OncePerRequestFilter {

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Autowired
//     private JwtTokenRepository tokenRepository;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     FilterChain filterChain) throws ServletException, IOException {

//         final String authHeader = request.getHeader("Authorization");

//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             String token = authHeader.substring(7);

//             // Check if token exists in DB
//             if (tokenRepository.findByToken(token).isEmpty()) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 return;
//             }

//             // Check if token is expired
//             if (jwtUtil.isTokenExpired(token)) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 return;
//             }

//             // Extract claims and authenticate
//             Claims claims = jwtUtil.extractAllClaims(token);
//             String username = claims.getSubject();
//             String role = (String) claims.get("role");

//             SecurityContextHolder.getContext().setAuthentication(
//                 new UsernamePasswordAuthenticationToken(
//                     username,
//                     null,
//                     Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
//                 )
//             );
//         }

//         filterChain.doFilter(request, response);
//     }
// }

package com.rotido.backend.filter;

import com.rotido.backend.model.JwtToken;
import com.rotido.backend.repository.JwtTokenRepository;
import com.rotido.backend.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Skip JWT filter for open endpoints
        String path = request.getRequestURI();
        if (path.equals("/api/mqtt/send")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Check if token exists in DB and is not expired
                Optional<JwtToken> jwtTokenOpt = tokenRepository.findByToken(token);
                
                if (jwtTokenOpt.isEmpty()) {
                    System.out.println("Token not found in database");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Token not found\"}");
                    return;
                }

                JwtToken jwtToken = jwtTokenOpt.get();
                
                // Check if token is expired in database
                if (jwtToken.getExpiresAt().before(new Date())) {
                    System.out.println("Token expired in database");
                    // Delete expired token
                    tokenRepository.delete(jwtToken);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Token expired\"}");
                    return;
                }

                // Validate token using JWT util
                if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                    System.out.println("Token validation failed");
                    // Delete invalid token
                    tokenRepository.delete(jwtToken);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Invalid token\"}");
                    return;
                }

                // Extract claims and authenticate
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                String role = (String) claims.get("role");

                System.out.println("Authenticating user: " + username + " with role: " + role);

                // Create authentication with proper role
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );

                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                System.out.println("Error processing token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Token processing failed\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}