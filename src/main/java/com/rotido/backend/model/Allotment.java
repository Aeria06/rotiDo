// package com.rotido.backend.model;


// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.time.LocalDate;

// @Document(collection = "allotment")
// public class Allotment {

//     @Id
//     private String id;

//     private String machineId;
//     private String customerNumber;
//     private String status; // "sold" or "subscribed"

//     private LocalDate startDate; // for subscriptions
//     private LocalDate endDate;   // for subscriptions

//     private LocalDate dateOfBuying; // for sold

//     // Getters and Setters
//     public String getId() {
//         return id;
//     }

//     public void setId(String id) {
//         this.id = id;
//     }

//     public String getMachineId() {
//         return machineId;
//     }

//     public void setMachineId(String machineId) {
//         this.machineId = machineId;
//     }

//     public String getCustomerNumber() {
//         return customerNumber;
//     }

//     public void setCustomerNumber(String customerNumber) {
//         this.customerNumber = customerNumber;
//     }

//     public String getStatus() {
//         return status;
//     }

//     public void setStatus(String status) {
//         this.status = status;
//     }

//     public LocalDate getStartDate() {
//         return startDate;
//     }

//     public void setStartDate(LocalDate startDate) {
//         this.startDate = startDate;
//     }

//     public LocalDate getEndDate() {
//         return endDate;
//     }

//     public void setEndDate(LocalDate endDate) {
//         this.endDate = endDate;
//     }

//     public LocalDate getDateOfBuying() {
//         return dateOfBuying;
//     }

//     public void setDateOfBuying(LocalDate dateOfBuying) {
//         this.dateOfBuying = dateOfBuying;
//     }
// }

package com.rotido.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "allotments")
public class Allotment {

    @Id
    private String id;

    private String machineId;
    private String customerNumber;
    private String status; // "subscribed" or "sold"

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dateOfBuying;

    // --- Getters ---
    public String getId() { return id; }
    public String getMachineId() { return machineId; }
    public String getCustomerNumber() { return customerNumber; }
    public String getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getDateOfBuying() { return dateOfBuying; }

    // --- Setters ---
    public void setMachineId(String machineId) { this.machineId = machineId; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setDateOfBuying(LocalDate dateOfBuying) { this.dateOfBuying = dateOfBuying; }
}
