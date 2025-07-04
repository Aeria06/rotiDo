// package com.rotido.backend.model;

// import java.time.LocalDate;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// @Document(collection = "sold_machines")
// public class SoldMachine {

//     @Id
//     private String id;

//     private String machineId;
//     private String clientNumber;
//     // private LocalDate dateOfBuying;
//     private String latitude;
//     private String longitude;
//     private String address;
//     private String machineStatus; // enable / disable

//     // ✅ Setters
//     public void setMachineId(String machineId) {
//         this.machineId = machineId;
//     }

//     public void setClientNumber(String clientNumber) {
//         this.clientNumber = clientNumber;
//     }

//     public void setDateOfBuying(LocalDate dateOfBuying) {
//         this.dateOfBuying = dateOfBuying;
//     }

//     public void setMachineStatus(String machineStatus) {
//         this.machineStatus = machineStatus;
//     }

//     public void setLatitude(String latitude) {
//         this.latitude = latitude;
//     }

//     public void setLongitude(String longitude) {
//         this.longitude = longitude;
//     }

//     public void setAddress(String address) {
//         this.address = address;
//     }

//     // ✅ Getters
//     public String getId() {
//         return id;
//     }

//     public String getMachineId() {
//         return machineId;
//     }

//     public String getClientNumber() {
//         return clientNumber;
//     }

//     public LocalDate getDateOfBuying() {
//         return dateOfBuying;
//     }

//     public String getLatitude() {
//         return latitude;
//     }

//     public String getLongitude() {
//         return longitude;
//     }

//     public String getAddress() {
//         return address;
//     }

//     public String getMachineStatus() {
//         return machineStatus;
//     }
// }

package com.rotido.backend.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sold_machines")
public class SoldMachine {

    @Id
    private String id;

    private String machineId;
    private String clientNumber;
    private LocalDate dateOfBuying;
    private String latitude;
    private String longitude;
    private String address;

    // enable or disable
    private String machineStatus;

    // ✅ Setters
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public void setDateOfBuying(LocalDate dateOfBuying) {
        this.dateOfBuying = dateOfBuying;
    }

    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ✅ Getters
    public String getId() {
        return id;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public LocalDate getDateOfBuying() {
        return dateOfBuying;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getMachineStatus() {
        return machineStatus;
    }
}
 