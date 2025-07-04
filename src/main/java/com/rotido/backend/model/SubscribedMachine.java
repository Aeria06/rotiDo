package com.rotido.backend.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "subscribed_machines")
public class SubscribedMachine {
    @Id
    private String id;
    private String clientNumber;                                                                                                       
    private String machineId;
    private String latitudeRange;
    private String longitudeRange;
    private int RotisAlloted;
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
    private boolean renewalApplied;
    private String machineStatus; // "enable" or "disable"
    private String freeze; 
    
    
}
    