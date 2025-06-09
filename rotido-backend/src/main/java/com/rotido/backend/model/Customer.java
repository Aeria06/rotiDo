package com.rotido.backend.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "customer_database")
public class Customer {
    @Id
    private String customerNumber;
    private String customerName;
    private String mail;
    private String phoneNumber;
    private String address;
    private String latitudeLongitude;
}