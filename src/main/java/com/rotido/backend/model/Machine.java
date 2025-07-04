package com.rotido.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;


import java.time.Instant;

@Document(collection = "machines")
public class Machine {

    @Id
    @Indexed(unique = true)
    private String id;

    private String machineId;

    private Instant dateOfManufacturing;

    private String status; // "subscribed" or "sold" OR "INVENTORY"

    private long totalChapatisMade;

    private int masterStatus; // 0 or 1, indicates master switch status

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public Instant getDateOfManufacturing() {
        return dateOfManufacturing;
    }

    public void setDateOfManufacturing(Instant dateOfManufacturing) {
        this.dateOfManufacturing = dateOfManufacturing;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalChapatisMade() {
        return totalChapatisMade;
    }

    public void setTotalChapatisMade(long totalChapatisMade) {
        this.totalChapatisMade = totalChapatisMade;
    }

    public int getMasterStatus() {
        return masterStatus;
    }

    public void setMasterStatus(int masterStatus) {
        this.masterStatus = masterStatus;
    }
}
