package com.rotido.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "machineNoti")
public class MachineNotification {
    @Id
    private String id;
    private String machineId;
    private String ts;
    private int tp;
    private String ms;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }
    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }
    public int getTp() { return tp; }
    public void setTp(int tp) { this.tp = tp; }
    public String getMs() { return ms; }
    public void setMs(String ms) { this.ms = ms; }
}
