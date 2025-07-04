package com.rotido.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_providers")
public class ServiceProvider {

    @Id
    private String id;

    private String username;

    private String name;
    private String contact;
    private String area;

    private String email;

public String getUsername() { return username; }
public void setUsername(String username) { this.username = username; }

    // No-arg constructor (important for Jackson)
    public ServiceProvider() {}

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getArea() {
        return area;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
