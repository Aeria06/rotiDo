// package com.rotido.backend.security;

// import io.jsonwebtoken.*;
// import org.springframework.stereotype.Component;

// import java.util.Date;

// @Component
// public class JwtUtil {
//     private final String SECRET_KEY = "rotido_secret";

//     public String generateToken(String username, String role) {
//         return Jwts.builder()
//             .setSubject(username)
//             .claim("role", role)
//             .setIssuedAt(new Date())
//             .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
//             .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//             .compact();
//     }

//     public String extractUsername(String token) {
//         return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token)
//                 .getBody().getSubject();
//     }

//     public boolean validateToken(String token) {
//         try {
//             Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
//             return true;
//         } catch (JwtException e) {
//             return false;
//         }
//     }
// }

package com.rotido.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "your-very-secure-and-long-secret-key-of-at-least-32-bytes";

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username, String role) {
        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
