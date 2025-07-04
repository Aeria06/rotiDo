package com.rotido.backend.controller;

import com.rotido.backend.model.Complaint;
import com.rotido.backend.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/dashboard/customer/complaint")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    // DELETE complaint by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable String id) {
        if (!complaintRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Complaint not found");
        }
        complaintRepository.deleteById(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }

    // TRACK complaint by ID
    @PostMapping("/track/{id}")
    public ResponseEntity<?> trackComplaint(@PathVariable String id) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Complaint not found");
        }
        return ResponseEntity.ok(complaintOpt.get());
    }

    // CREATE complaint for customer
    @PostMapping("")
    public ResponseEntity<?> createComplaint(@RequestBody Complaint complaint) {
        Complaint savedComplaint = complaintRepository.save(complaint);
        return ResponseEntity.ok(savedComplaint);
    }
}
