package com.rotido.backend.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "machine_dashboard")
public class Machine {
    @Id
    private String machineId;
    private LocalDate dateOfManufacturing;
    private String status; // sold / subscribed / vacant
    private List<MachineResponse> machineResponses;

    // Getters and Setters

    public static class MachineResponse {
        private LocalDate responseDate;
        private String responseDetail;

        // Getters and Setters
    }

    // Constructors, Getters, Setters
}
