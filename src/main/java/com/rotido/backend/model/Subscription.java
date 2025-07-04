// // package com.rotido.backend.model;


// // import org.springframework.data.annotation.Id;
// // import org.springframework.data.mongodb.core.mapping.Document;

// // import java.time.LocalDate;

// // @Document(collection = "subscriptions")
// // public class Subscription {

// //     @Id
// //     private String subscriptionId;
// //     private String machineId;
// //     private String customerNumber;
// //     private LocalDate startDate;
// //     private LocalDate endDate;

// //     // Getters and Setters
// //     public String getSubscriptionId() { return subscriptionId; }
// //     public String getMachineId() { return machineId; }
// //     public String getCustomerNumber() { return customerNumber; }
// //     public LocalDate getStartDate() { return startDate; }
// //     public LocalDate getEndDate() { return endDate; }
// //     public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
// //     public void setMachineId(String machineId) { this.machineId = machineId; }
// //     public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
// //     public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
// //     public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
// // }

// package com.rotido.backend.model;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.time.LocalDate;
// import java.time.LocalTime;

// @Document(collection = "subscriptions")
// public class Subscription {

//     @Id
//     private String id;

//     private String machineId;
//     private String clientNumber;
//     private LocalDate startDate;
//     private LocalDate endDate;
//     private LocalTime endTime;
//     private int rotisAllotted;
//     private String address;
//     private String latitude;
//     private String longitude;
//     private boolean renewalApplied;
//     private String machineStatus; // "enable" or "disable"
//     private String freeze;

//     // ✅ Setters
//     public void setMachineId(String machineId) {
//         this.machineId = machineId;
//     }

//     public void setClientNumber(String clientNumber) {
//         this.clientNumber = clientNumber;
//     }

//     public void setStartDate(LocalDate startDate) {
//         this.startDate = startDate;
//     }

//     public void setEndDate(LocalDate endDate) {
//         this.endDate = endDate;
//     }

//     public void setEndTime(LocalTime endTime) {
//         this.endTime = endTime;
//     }

//     public void setRotisAllotted(int rotisAllotted) {
//         this.rotisAllotted = rotisAllotted;
//     }

//     public void setAddress(String address) {
//         this.address = address;
//     }

//     public void setLatitude(String latitude) {
//         this.latitude = latitude;
//     }

//     public void setLongitude(String longitude) {
//         this.longitude = longitude;
//     }

//     public void setRenewalApplied(boolean renewalApplied) {
//         this.renewalApplied = renewalApplied;
//     }

//     public void setMachineStatus(String machineStatus) {
//         this.machineStatus = machineStatus;
//     }

//     public void setFreeze(String freeze) {
//         this.freeze = freeze;
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

//     public LocalDate getStartDate() {
//         return startDate;
//     }

//     public LocalDate getEndDate() {
//         return endDate;
//     }

//     public LocalTime getEndTime() {
//         return endTime;
//     }

//     public int getRotisAllotted() {
//         return rotisAllotted;
//     }

//     public String getAddress() {
//         return address;
//     }

//     public String getLatitude() {
//         return latitude;
//     }

//     public String getLongitude() {
//         return longitude;
//     }

//     public boolean isRenewalApplied() {
//         return renewalApplied;
//     }

//     public String getMachineStatus() {
//         return machineStatus;
//     }

//     public String getFreeze() {
//         return freeze;
//     }
// }

package com.rotido.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String id;

    private String machineId;
    private String customerNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime endTime;
    private int rotisAllotted;
    private String address;
    private String latitude;
    private String longitude;
    private boolean renewalApplied;
    private String machineStatus; // "enable" or "disable"
    private String freeze;
    private String renewal; // "yes" or "no"
    private int renewalChapatis;
    private LocalDate renewalStart;
    private LocalDate renewalEnd;
    private int rotisMade; // Number of chapatis made for this subscription
    private int rotiLeft;
    private boolean enabled = true; // Whether the machine is enabled for this subscription

    // ✅ Setters
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setRotisAllotted(int rotisAllotted) {
        this.rotisAllotted = rotisAllotted;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setRenewalApplied(boolean renewalApplied) {
        this.renewalApplied = renewalApplied;
    }

    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    public void setRenewal(String renewal) {
        this.renewal = renewal;
    }

    public void setRenewalChapatis(int renewalChapatis) {
        this.renewalChapatis = renewalChapatis;
    }

    public void setRenewalStart(LocalDate renewalStart) {
        this.renewalStart = renewalStart;
    }

    public void setRenewalEnd(LocalDate renewalEnd) {
        this.renewalEnd = renewalEnd;
    }

    public void setRotisMade(int rotisMade) {
        this.rotisMade = rotisMade;
    }

    public void setRotiLeft(int rotiLeft) {
        this.rotiLeft = rotiLeft;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.machineStatus = enabled ? "enable" : "disable";
    }

    // ✅ Getters
    public String getId() {
        return id;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getRotisAllotted() {
        return rotisAllotted;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public boolean isRenewalApplied() {
        return renewalApplied;
    }

    public String getMachineStatus() {
        return machineStatus;
    }

    public String getFreeze() {
        return freeze;
    }

    public String getRenewal() {
        return renewal;
    }

    public int getRenewalChapatis() {
        return renewalChapatis;
    }

    public LocalDate getRenewalStart() {
        return renewalStart;
    }

    public LocalDate getRenewalEnd() {
        return renewalEnd;
    }

    public int getRotisMade() {
        return rotisMade;
    }

    public int getRotiLeft() {
        return rotisAllotted - rotisMade;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
