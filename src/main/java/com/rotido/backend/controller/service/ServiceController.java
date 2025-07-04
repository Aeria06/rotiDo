package com.rotido.backend.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rotido.backend.model.Complaint;
import com.rotido.backend.model.ServiceProvider;
import com.rotido.backend.model.Customer;
import com.rotido.backend.model.Machine;
import com.rotido.backend.repository.ComplaintRepository;
import com.rotido.backend.repository.ServiceProviderRepository;
import com.rotido.backend.repository.CustomerRepository;
import com.rotido.backend.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/service")
public class ServiceController {
    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private ServiceProviderRepository serviceProviderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private MachineRepository machineRepository;

    // Get profile by username
    @PostMapping("/profile/{username}")
    public ResponseEntity<?> getServiceProviderProfile(@PathVariable String username) {
        Optional<ServiceProvider> spOpt = serviceProviderRepository.findByUsername(username);
        System.out.println("[DEBUG] Fetching profile for username: " + username);
        if (spOpt.isEmpty()) {
            System.out.println("[DEBUG] Service provider not found for username: " + username);
            return ResponseEntity.status(404).body("Service provider not found");
        }
        ServiceProvider sp = spOpt.get();
        System.out.println("[DEBUG] ServiceProvider fetched: " + sp);
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", sp.getName());
        profile.put("email", sp.getEmail());
        profile.put("phone", sp.getContact());
        profile.put("specialization", sp.getArea());
        profile.put("experience", "N/A"); // Add logic if you have experience field
        profile.put("rating", 4.8); // Placeholder, add logic if you have rating
        profile.put("completedJobs", 0); // Placeholder, add logic if you have jobs
        profile.put("location", sp.getArea());
        System.out.println("[DEBUG] Profile response: " + profile);
        return ResponseEntity.ok(profile);
    }

    // Get all complaints assigned to this service provider, enriched with customer and machine info
    @PostMapping("/complaints/assigned/{username}")
    public ResponseEntity<?> getComplaintsForServiceProvider(@PathVariable String username) {
        List<Complaint> complaints = complaintRepository.findAllByAssignedTo(username);
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Complaint c : complaints) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("customerNumber", c.getCustomerNumber());
            map.put("machineId", c.getMachineId());
            map.put("subject", c.getSubject());
            map.put("description", c.getDescription());
            map.put("status", c.getStatus());
            map.put("priority", c.getPriority());
            map.put("createdAt", c.getCreatedAt());
            map.put("assignedTo", c.getAssignedTo());
            map.put("notes", c.getNotes());
            // Enrich with customer (use correct field names)
            Customer customer = null;
            try {
                customer = customerRepository.findByCustomerNumber(c.getCustomerNumber());
            } catch (Exception ignored) {}
            if (customer != null) {
                Map<String, Object> cust = new HashMap<>();
                cust.put("customerName", customer.getCustomerName());
                cust.put("phoneNumber", customer.getPhoneNumber());
                cust.put("address", customer.getAddress());
                map.put("customer", cust);
            } else {
                map.put("customer", null);
            }
            // Enrich with machine (add model if available, else just serial and status)
            Machine machine = null;
            try {
                machine = machineRepository.findByMachineId(c.getMachineId()).orElse(null);
            } catch (Exception ignored) {}
            if (machine != null) {
                Map<String, Object> mach = new HashMap<>();
                // If you have a model field, use it. Otherwise, just use machineId and status.
                // mach.put("model", machine.getModel()); // Uncomment if model exists
                mach.put("serialNumber", machine.getMachineId());
                mach.put("status", machine.getStatus());
                map.put("machine", mach);
            } else {
                map.put("machine", null);
            }
            enriched.add(map);
        }
        return ResponseEntity.ok(enriched);
    }

    // Update complaint status (with optional notes)
    public static class ComplaintStatusUpdateRequest {
        private String status;
        private Note notes;
        // getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Note getNotes() { return notes; }
        public void setNotes(Note notes) { this.notes = notes; }
        public static class Note {
            private String text;
            private String timestamp;
            // getters and setters
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
            public String getTimestamp() { return timestamp; }
            public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        }
    }

    @PostMapping("/complaints/{id}/status")
    public ResponseEntity<?> updateComplaintStatusByService(@PathVariable String id, @RequestBody ComplaintStatusUpdateRequest payload) {
        String status = payload.getStatus();
        ComplaintStatusUpdateRequest.Note notesObj = payload.getNotes();
        ObjectMapper mapper = new ObjectMapper();
        List<Object> notesList = new ArrayList<>();
        try {
            String existingNotes = null;
            Optional<Complaint> complaintOpt = complaintRepository.findById(id);
            if (complaintOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Complaint not found");
            }
            Complaint complaint = complaintOpt.get();
            existingNotes = complaint.getNotes();
            if (existingNotes != null && !existingNotes.isEmpty()) {
                // Try to parse as array, or as single object
                if (existingNotes.trim().startsWith("[")) {
                    notesList = mapper.readValue(existingNotes, new TypeReference<List<Object>>(){});
                } else if (existingNotes.trim().startsWith("{")) {
                    // Convert single object to array
                    Object singleNote = mapper.readValue(existingNotes, Object.class);
                    notesList.add(singleNote);
                }
            }
            if (notesObj != null) {
                // Add new note
                notesList.add(notesObj);
            }
            complaint.setStatus(status);
            if (!notesList.isEmpty()) {
                complaint.setNotes(mapper.writeValueAsString(notesList));
            }
            complaintRepository.save(complaint);
            return ResponseEntity.ok("Complaint status updated to " + status);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating notes: " + e.getMessage());
        }
    }
}
