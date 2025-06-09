
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
    private String machineStatus; // enable / disable
    
}
