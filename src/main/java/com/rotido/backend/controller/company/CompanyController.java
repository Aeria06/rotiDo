package com.rotido.backend.controller.company;

import com.rotido.backend.model.*;
import com.rotido.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired private MachineRepository machineRepository;
    @Autowired private AllotmentRepository allotmentRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ServiceProviderRepository serviceProviderRepository;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MachineNotificationRepository machineNotificationRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private SoldMachineRepository soldMachineRepository;
    @Autowired private ComplaintRepository complaintRepository; // Add this line

    // ---------- Helper ----------
    private double safeParse(String val) {
        try {
            if (val == null || val.trim().isEmpty()) return 0.0;
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ---------- MACHINE ENDPOINTS ----------

    @PostMapping("/machine/add")
    public ResponseEntity<?> addMachine(@RequestBody Machine machine) {
        try {
            if (machine.getMachineId() == null || machine.getMachineId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Machine ID is required");
            }
            if (machineRepository.findAll().stream().anyMatch(m -> m.getMachineId() != null && m.getMachineId().equals(machine.getMachineId()))) {
                return ResponseEntity.badRequest().body("Machine ID already exists");
            }
            return ResponseEntity.ok(machineRepository.save(machine));
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(400).body("Error adding machine: " + e.getMessage());
        }
    }

    @PostMapping("/machine/list")
    public ResponseEntity<List<Machine>> listMachines() {
        return ResponseEntity.ok(machineRepository.findAll());
    }

    @DeleteMapping("/machine/delete/{machineId}")
    public ResponseEntity<?> deleteMachine(@PathVariable String machineId) {
        List<Machine> toDelete = machineRepository.findAll().stream().filter(m -> m.getMachineId().equals(machineId)).toList();
        if (toDelete.isEmpty()) {
            return ResponseEntity.status(404).body("Machine not found");
        }
        toDelete.forEach(m -> machineRepository.deleteById(m.getId()));
        return ResponseEntity.ok("Deleted " + toDelete.size() + " machine(s)");
    }
    @PutMapping("/machine/update-by-machineId/{machineId}")
    public ResponseEntity<?> updateMachineByMachineId(@PathVariable String machineId, @RequestBody Map<String, Object> updates) {
        Optional<Machine> optionalMachine = machineRepository.findByMachineId(machineId);
        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();
            // Only update fields that are present and not null
            if (updates.containsKey("dateOfManufacturing") && updates.get("dateOfManufacturing") != null) {
                machine.setDateOfManufacturing((java.time.Instant) updates.get("dateOfManufacturing"));
            }
            if (updates.containsKey("status") && updates.get("status") != null) {
                machine.setStatus((String) updates.get("status"));
            }
            if (updates.containsKey("totalChapatisMade") && updates.get("totalChapatisMade") != null) {
                machine.setTotalChapatisMade(Long.parseLong(updates.get("totalChapatisMade").toString()));
            }
            if (updates.containsKey("masterStatus") && updates.get("masterStatus") != null) {
                machine.setMasterStatus(Integer.parseInt(updates.get("masterStatus").toString()));
            }
            // Do NOT update machineId here to prevent nulling it out
            return ResponseEntity.ok(machineRepository.save(machine));
        }
        return ResponseEntity.status(404).body("Machine not found");
    }

    @PostMapping("/machine/{machineId}")
    public ResponseEntity<List<MachineNotification>> getMachineNotifications(@PathVariable String machineId) {
        return ResponseEntity.ok(machineNotificationRepository.findByMachineId(machineId));
    }

    @PostMapping("/transaction/add")
public ResponseEntity<?> addTransaction(@RequestBody Map<String, Object> payload) {
    String machineId = (String) payload.get("machineId");
    String customerNumber = (String) payload.get("customerNumber");
    String status = (String) payload.get("status");

    try {
        Allotment allotment = new Allotment();
        allotment.setMachineId(machineId);
        allotment.setCustomerNumber(customerNumber);
        allotment.setStatus(status);

        // Fetch machine from DB
    Optional<Machine> machineOpt = machineRepository.findByMachineId(machineId);
        if (machineOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Machine not found");
        }
        Machine machine = machineOpt.get(); // ✅ safely unwrapped


        if ("subscribed".equalsIgnoreCase(status)) {
            Subscription sub = new Subscription();
            sub.setMachineId(machineId);
            sub.setCustomerNumber(customerNumber);
            // Robust date parsing for ISO 8601 with time
            sub.setStartDate(java.time.Instant.parse((String) payload.get("startDate")).atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            sub.setEndDate(java.time.Instant.parse((String) payload.get("endDate")).atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            subscriptionRepository.save(sub);

            allotment.setStartDate(sub.getStartDate());
            allotment.setEndDate(sub.getEndDate());

            // Update machine status
            machine.setStatus("subscribed");

        } else if ("sold".equalsIgnoreCase(status)) {
            SoldMachine sold = new SoldMachine();
            sold.setMachineId(machineId);
            sold.setClientNumber(customerNumber);
            sold.setDateOfBuying(java.time.Instant.parse((String) payload.get("date")).atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            sold.setMachineStatus("sold");
            soldMachineRepository.save(sold);

            allotment.setDateOfBuying(sold.getDateOfBuying());

            // Update machine status
            machine.setStatus("sold");

        } else {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        // Save changes to machine and allotment
        machineRepository.save(machine);
        allotmentRepository.save(allotment);

        return ResponseEntity.ok("Transaction and allotment added successfully");

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}


    @PutMapping("/transaction/edit/{machineId}")
    public ResponseEntity<?> editTransaction(@PathVariable String machineId, @RequestBody Map<String, Object> payload) {
        try {
            List<Allotment> allotments = allotmentRepository.findAllByMachineId(machineId);
            if (allotments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No allotment found for machineId: " + machineId);
            }
            Allotment allotment = allotments.get(0); // Use the first or handle as needed

            if ("sold".equalsIgnoreCase(allotment.getStatus())) {
                List<SoldMachine> solds = soldMachineRepository.findAllByMachineId(machineId);
                if (solds.isEmpty()) return ResponseEntity.status(404).body("SoldMachine entry not found");
                SoldMachine sold = solds.get(0); // Use the first or handle as needed
                sold.setAddress((String) payload.get("address"));
                sold.setLatitude((String) payload.get("latitude"));
                sold.setLongitude((String) payload.get("longitude"));
                soldMachineRepository.save(sold);
            } else if ("subscribed".equalsIgnoreCase(allotment.getStatus())) {
                List<Subscription> subs = subscriptionRepository.findAllByMachineId(machineId);
                if (subs.isEmpty()) return ResponseEntity.status(404).body("Subscription entry not found");
                Subscription sub = subs.get(0); // Use the first or handle as needed
                if (payload.get("endDate") != null) {
                    sub.setEndDate(java.time.Instant.parse((String) payload.get("endDate")).atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                }
                if (payload.get("endTime") != null) {
                    sub.setEndTime(LocalTime.parse((String) payload.get("endTime")));
                }
                if (payload.get("rotisAllotted") != null) {
                    sub.setRotisAllotted(Integer.parseInt(payload.get("rotisAllotted").toString()));
                }
                if (payload.get("address") != null) {
                    sub.setAddress((String) payload.get("address"));
                }
                if (payload.get("latitude") != null) {
                    sub.setLatitude((String) payload.get("latitude"));
                }
                if (payload.get("longitude") != null) {
                    sub.setLongitude((String) payload.get("longitude"));
                }
                subscriptionRepository.save(sub);
            } else {
                return ResponseEntity.badRequest().body("Invalid status in allotment");
            }

            return ResponseEntity.ok("Transaction updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/transaction/delete/{machineId}")
public ResponseEntity<?> deleteTransaction(@PathVariable String machineId) {
    try {
        // 1. Delete from allotment
        List<Allotment> allotments = allotmentRepository.findAllByMachineId(machineId);
        if (allotments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No allotment found");
        }
        Allotment allotment = allotments.get(0); // or handle multiple as needed
        String status = allotment.getStatus();

        // 2. Delete from corresponding status table
        if ("subscribed".equalsIgnoreCase(status)) {
            List<Subscription> subs = subscriptionRepository.findAllByMachineId(machineId);
            if (!subs.isEmpty()) {
                subscriptionRepository.delete(subs.get(0));
            }
        } else if ("sold".equalsIgnoreCase(status)) {
            List<SoldMachine> solds = soldMachineRepository.findAllByMachineId(machineId);
            if (!solds.isEmpty()) {
                soldMachineRepository.delete(solds.get(0));
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        // 3. Delete the allotment
        allotmentRepository.delete(allotment);

        // 4. Update Machine status to INVENTORY
        Optional<Machine> machineOpt = machineRepository.findByMachineId(machineId);
        if (machineOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Machine not found");
        }
        Machine machine = machineOpt.get();

        return ResponseEntity.ok("Transaction deleted and machine returned to inventory");

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}


    @PostMapping("/transaction/list")
    public ResponseEntity<?> listTransactions() {
        try {
            List<Map<String, Object>> transactions = new ArrayList<>();

            for (Allotment allotment : allotmentRepository.findAll()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", allotment.getId());
                transaction.put("machineId", allotment.getMachineId());
                transaction.put("customerNumber", allotment.getCustomerNumber());
                transaction.put("status", allotment.getStatus());
                transaction.put("enabled", true);
                transaction.put("freeze", false);

                customerRepository.findByPhoneNumber(allotment.getCustomerNumber())
                        .ifPresentOrElse(
                                c -> transaction.put("customerName", c.getCustomerName()),
                                () -> transaction.put("customerName", "Unknown Customer"));

                Map<String, Object> location = new HashMap<>();
                location.put("isLocked", "sold".equalsIgnoreCase(allotment.getStatus()));

                if ("subscribed".equalsIgnoreCase(allotment.getStatus())) {
                    List<Subscription> subs = subscriptionRepository.findAllByMachineId(allotment.getMachineId());
                    if (!subs.isEmpty()) {
                        Subscription sub = subs.get(0); // Use the first or handle as needed
                        transaction.put("startDate", sub.getStartDate() != null ? sub.getStartDate().atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toString() : null);
                        transaction.put("endDate", sub.getEndDate() != null ? sub.getEndDate().atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toString() : null);
                        transaction.put("endTime", sub.getEndTime() != null ? sub.getEndTime().toString() : "23:59");
                        transaction.put("rotisAllotted", sub.getRotisAllotted());
                        location.put("address", sub.getAddress() != null ? sub.getAddress() : "");
                        location.put("latitude", safeParse(sub.getLatitude()));
                        location.put("longitude", safeParse(sub.getLongitude()));
                    }
                } else if ("sold".equalsIgnoreCase(allotment.getStatus())) {
                    List<SoldMachine> solds = soldMachineRepository.findAllByMachineId(allotment.getMachineId());
                    if (!solds.isEmpty()) {
                        SoldMachine sold = solds.get(0); // Use the first or handle as needed
                        transaction.put("startDate", sold.getDateOfBuying() != null ? sold.getDateOfBuying().atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toString() : null);
                        location.put("address", sold.getAddress() != null ? sold.getAddress() : "");
                        location.put("latitude", safeParse(sold.getLatitude()));
                        location.put("longitude", safeParse(sold.getLongitude()));
                    }
                }

                location.putIfAbsent("address", "");
                location.putIfAbsent("latitude", 0.0);
                location.putIfAbsent("longitude", 0.0);
                transaction.put("location", location);

                transactions.add(transaction);
            }

            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching transactions: " + e.getMessage());
        }
    }

// ---------- CUSTOMER ENDPOINTS ----------

@PostMapping("/customer/add")
public ResponseEntity<?> addCustomer(@RequestBody Map<String, String> payload) {
    try {
        String username = payload.get("username");
        String password = payload.get("password");
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setCustomerName(payload.get("customerName"));
        customer.setMail(payload.get("mail"));
        customer.setPhoneNumber(payload.get("phoneNumber"));
        customer.setAddress(payload.get("address"));
        customer.setLatitudeLongitude(payload.get("latitudeLongitude"));
        // Generate and set customerNumber if not present
        String customerNumber = UUID.randomUUID().toString();
        customer.setCustomerNumber(customerNumber);
        customerRepository.save(customer);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("customer");
        userRepo.save(user);
        return ResponseEntity.ok("Customer and user registered successfully");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}

@PostMapping("/customer/list")
public ResponseEntity<List<Customer>> listCustomers() {
    return ResponseEntity.ok(customerRepository.findAll());
}

@DeleteMapping("/customer/delete/{username}")
public ResponseEntity<?> deleteCustomer(@PathVariable String username) {
    customerRepository.deleteByUsername(username);
    userRepo.deleteByUsername(username);
    return ResponseEntity.ok("Customer and user deleted successfully");
}

@PutMapping("/customer/update/{username}")
public ResponseEntity<?> updateCustomer(@PathVariable String username, @RequestBody Customer updatedCustomer) {
    return customerRepository.findByUsername(username).map(customer -> {
        customer.setCustomerName(updatedCustomer.getCustomerName());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setMail(updatedCustomer.getMail());
        return ResponseEntity.ok(customerRepository.save(customer));
    }).orElse(ResponseEntity.notFound().build());
}

// ---------- SERVICE PROVIDER ENDPOINTS ----------

@PostMapping("/service/add")
public ResponseEntity<?> addServiceProvider(@RequestBody Map<String, String> payload) {
    try {
        String username = payload.get("username");
        String password = payload.get("password");

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        ServiceProvider sp = new ServiceProvider();
        sp.setId(payload.get("id"));
        sp.setUsername(username);
        sp.setName(payload.get("name"));
        sp.setContact(payload.get("contact"));
        sp.setArea(payload.get("area"));
        serviceProviderRepository.save(sp);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("service");
        userRepo.save(user);

        return ResponseEntity.ok("Service provider and user registered successfully");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}

@PostMapping("/service/list")
public ResponseEntity<List<ServiceProvider>> listServiceProviders() {
    return ResponseEntity.ok(serviceProviderRepository.findAll());
}

@DeleteMapping("/service/delete/{username}")
public ResponseEntity<?> deleteServiceProvider(@PathVariable String username) {
    serviceProviderRepository.deleteByUsername(username);
    userRepo.deleteByUsername(username);
    return ResponseEntity.ok("Service provider and user deleted successfully");
}

@PutMapping("/service/update/{username}")
public ResponseEntity<?> updateServiceProvider(@PathVariable String username, @RequestBody ServiceProvider updatedSp) {
    return serviceProviderRepository.findByUsername(username).map(sp -> {
        sp.setName(updatedSp.getName());
        sp.setContact(updatedSp.getContact());
        sp.setArea(updatedSp.getArea());
        return ResponseEntity.ok(serviceProviderRepository.save(sp));
    }).orElse(ResponseEntity.notFound().build());
}

// ---------- ADMIN COMPLAINTS ENDPOINT ----------

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/complaints")
public ResponseEntity<?> getAllComplaints() {
    return ResponseEntity.ok(complaintRepository.findAll());
}

@PostMapping("/complaints/{id}")
public ResponseEntity<?> allotServiceProvider(@PathVariable String id, @RequestBody Map<String, String> payload) {
    String username = payload.get("username");
    if (username == null || username.isEmpty()) {
        return ResponseEntity.badRequest().body("Service provider username is required");
    }
    Optional<Complaint> complaintOpt = complaintRepository.findById(id);
    if (complaintOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Complaint not found");
    }
    Complaint complaint = complaintOpt.get();
    complaint.setAssignedTo(username);
    complaint.setStatus("ASSIGNED");
    complaintRepository.save(complaint);
    return ResponseEntity.ok("Service provider allotted and status updated to ASSIGNED");
}

@PutMapping("/complaints/{id}/status")
public ResponseEntity<?> updateComplaintStatus(@PathVariable String id, @RequestBody Map<String, String> payload) {
    String status = payload.get("status");
    if (status == null || status.isEmpty()) {
        return ResponseEntity.badRequest().body("Status is required");
    }
    Optional<Complaint> complaintOpt = complaintRepository.findById(id);
    if (complaintOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Complaint not found");
    }
    Complaint complaint = complaintOpt.get();
    complaint.setStatus(status);
    complaintRepository.save(complaint);
    return ResponseEntity.ok("Complaint status updated to " + status);
}

// Service provider: get all complaints assigned to them
@PostMapping("/complaints/assigned/{username}")
public ResponseEntity<?> getComplaintsForServiceProvider(@PathVariable String username) {
    return ResponseEntity.ok(complaintRepository.findAllByAssignedTo(username));
}

// Service provider: get profile by username
@GetMapping("/service/profile/{username}")
public ResponseEntity<?> getServiceProviderProfile(@PathVariable String username) {
    Optional<ServiceProvider> spOpt = serviceProviderRepository.findByUsername(username);
    if (spOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Service provider not found");
    }
    ServiceProvider sp = spOpt.get();
    Map<String, Object> profile = new HashMap<>();
    profile.put("name", sp.getName());
    profile.put("email", sp.getEmail());
    profile.put("phone", sp.getContact());
    profile.put("specialization", sp.getArea());
    profile.put("experience", "N/A"); // Add logic if you have experience field
    profile.put("rating", 4.8); // Placeholder, add logic if you have rating
    profile.put("completedJobs", 0); // Placeholder, add logic if you have jobs
    profile.put("location", sp.getArea());
    return ResponseEntity.ok(profile);
}

// Service provider: update complaint status (with optional notes)
@PutMapping("/complaints/{id}/service-status")
public ResponseEntity<?> updateComplaintStatusByService(@PathVariable String id, @RequestBody Map<String, String> payload) {
    String status = payload.get("status");
    String notes = payload.get("notes");
    if (status == null || status.isEmpty()) {
        return ResponseEntity.badRequest().body("Status is required");
    }
    Optional<Complaint> complaintOpt = complaintRepository.findById(id);
    if (complaintOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Complaint not found");
    }
    Complaint complaint = complaintOpt.get();
    complaint.setStatus(status);
    if (notes != null) complaint.setNotes(notes); // Add notes field to Complaint if needed
    complaintRepository.save(complaint);
    return ResponseEntity.ok("Complaint status updated to " + status);
}

@DeleteMapping("/complaints/{id}")
public ResponseEntity<?> deleteComplaint(@PathVariable String id) {
    Optional<Complaint> complaintOpt = complaintRepository.findById(id);
    if (complaintOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Complaint not found");
    }
    complaintRepository.deleteById(id);
    return ResponseEntity.ok("Complaint deleted successfully");
}
}