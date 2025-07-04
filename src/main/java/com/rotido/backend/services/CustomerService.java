package com.rotido.backend.services;

import com.rotido.backend.model.Allotment;
import com.rotido.backend.model.Complaint;
import com.rotido.backend.model.Machine;
import com.rotido.backend.model.Subscription;
import com.rotido.backend.repository.AllotmentRepository;
import com.rotido.backend.repository.ComplaintRepository;
import com.rotido.backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private AllotmentRepository allotmentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private com.rotido.backend.repository.MachineRepository machineRepository;

    public Map<String, Object> getCustomerDashboard(String customerNumber) {
        System.out.println("getCustomerDashboard called for customerNumber: " + customerNumber);
        Map<String, Object> response = new HashMap<>();

        // Fetch Allotments (instead of machines by customerNumber)
        List<Allotment> allotments = allotmentRepository.findByCustomerNumber(customerNumber);
        // Fetch Subscriptions
        List<Subscription> subscriptions = subscriptionRepository.findByCustomerNumber(customerNumber);
        // Fetch Complaints
        List<Complaint> complaints = complaintRepository.findByCustomerNumber(customerNumber);

        // Build machine response with subscription info
        List<Map<String, Object>> machineData = new ArrayList<>();
        for (Allotment allotment : allotments) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", allotment.getId());
            m.put("serialNumber", allotment.getMachineId());
            Optional<Subscription> sub = subscriptions.stream()
                    .filter(s -> s.getMachineId().equals(allotment.getMachineId()))
                    .findFirst();
            if (sub.isPresent()) {
                Subscription s = sub.get();
                System.out.println("DEBUG: Subscription for machine " + allotment.getMachineId() + " has id: " + s.getId());
                System.out.println("DEBUG: Subscription object: " + s);
                m.put("type", "SUBSCRIBED");
                // Fetch machine and set rotisMade
                int rotisMade = 0;
                try {
                    Optional<Machine> machineOpt = machineRepository.findByMachineId(allotment.getMachineId());
                    if (machineOpt.isPresent()) {
                        rotisMade = (int) machineOpt.get().getTotalChapatisMade();
                    }
                } catch (Exception e) { /* ignore */ }
                m.put("subscription", Map.of(
                    "id", s.getId(),
                    "rotisMade", rotisMade
                ));
            } else {
                m.put("type", "BOUGHT");
                m.put("subscription", Map.of(
                    "id", null,
                    "rotisMade", 0
                ));
            }
            m.put("alerts", List.of()); // Placeholder for alerts
            machineData.add(m);
        }

        // Build complaints response
        List<Map<String, Object>> complaintData = new ArrayList<>();
        for (Complaint c : complaints) {
            complaintData.add(Map.of(
                    "id", c.getId(),
                    "subject", c.getSubject(),
                    "status", c.getStatus(),
                    "createdAt", c.getCreatedAt().toString(),
                    "assignedTo", c.getAssignedTo()
            ));
        }

        response.put("machines", machineData);
        response.put("complaints", complaintData);

        return response;
    }
}
