//working verwsion

// package com.rotido.backend.security;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Component;

// import java.nio.charset.StandardCharsets;
// import java.security.Key;
// import java.util.Date;

// @Component
// public class JwtUtil {

//     private static final String SECRET_KEY = "your-very-secure-and-long-secret-key-of-at-least-32-bytes";

//     private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

//     public String generateToken(String username, String role) {
//         return Jwts.builder()
//             .setSubject(username)
//             .claim("role", role)
//             .setIssuedAt(new Date())
//             .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
//             .signWith(key, SignatureAlgorithm.HS256)
//             .compact();
//     }

//     public String extractUsername(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getSubject();
//     }

//     public boolean validateToken(String token) {
//         try {
//             Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token);
//             return true;
//         } catch (JwtException e) {
//             return false;
//         }
//     }
// }

package com.rotido.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString;

    private Key secretKey;

    @PostConstruct
    public void init() {
        // Convert secret string into a key
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

   public String generateToken(String username, String role, Date expiration) {
    return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(expiration)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
}

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // just to trigger exception if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
