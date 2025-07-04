package com.rotido.backend.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "complaints")
public class Complaint {

    @Id
    private String id;

    private String customerNumber;
    private String machineId;
    private String subject;
    private String description;
    private String status;  // e.g., PENDING, ASSIGNED, IN_PROGRESS, RESOLVED
    private String priority;
    private LocalDate createdAt;
    private String assignedTo;
    private String notes;

    // Getters
    public String getId() {
        return id;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
