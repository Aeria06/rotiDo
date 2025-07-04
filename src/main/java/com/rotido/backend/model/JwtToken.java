// package com.rotido.backend.model;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.util.Date;

// @Document(collection = "tokens")
// public class JwtToken {

//     @Id
//     private String id;

//     private String token;
//     private String username;
//     private Date expiresAt;

//     // Getters and setters
//     public String getId() { return id; }
//     public String getToken() { return token; }
//     public String getUsername() { return username; }
//     public Date getExpiresAt() { return expiresAt; }

//     public void setId(String id) { this.id = id; }
//     public void setToken(String token) { this.token = token; }
//     public void setUsername(String username) { this.username = username; }
//     public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
// }

package com.rotido.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "tokens")
public class JwtToken {

    @Id
    private String id;

    private String token;
    private String username;
    private String role; // Add role field
    private Date expiresAt;
    private Date createdAt;

    public JwtToken() {
        this.createdAt = new Date();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Date getExpiresAt() { return expiresAt; }
    public Date getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setToken(String token) { this.token = token; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}