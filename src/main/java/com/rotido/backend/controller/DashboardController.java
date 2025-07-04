package com.rotido.backend.controller;

import com.rotido.backend.dto.company.DashboardResponse;
import com.rotido.backend.services.company.CompanyService;
import com.rotido.backend.services.CustomerService;
import com.rotido.backend.model.Customer;
import com.rotido.backend.model.Subscription;
import com.rotido.backend.model.Complaint;
import com.rotido.backend.repository.CustomerRepository;
import com.rotido.backend.repository.SubscriptionRepository;
import com.rotido.backend.repository.ComplaintRepository;
import com.rotido.backend.repository.MachineNotificationRepository;
import com.rotido.backend.repository.MachineRepository;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private MachineNotificationRepository machineNotificationRepository;

    @Autowired
    private MachineRepository machineRepository;

    @PostMapping("/company")
    public ResponseEntity<?> adminDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Admin Dashboard");
        response.put("user", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("timestamp", System.currentTimeMillis());

        try {
            DashboardResponse dashboardData = companyService.getDashboardData();
            response.put("dashboard", dashboardData);
        } catch (Exception e) {
            response.put("dashboard", "Error fetching dashboard data: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    

    @PostMapping("/customer")
    public ResponseEntity<?> customerDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerNumber = auth.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Customer Dashboard");
        response.put("user", customerNumber);
        response.put("authorities", auth.getAuthorities());
        response.put("timestamp", System.currentTimeMillis());

        try {
            Map<String, Object> customerDashboard = customerService.getCustomerDashboard(customerNumber);
            response.putAll(customerDashboard);
        } catch (Exception e) {
            response.put("error", "Error fetching customer dashboard data: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/service")
    public ResponseEntity<?> serviceDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Service Dashboard");
        response.put("user", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("authenticated", auth.isAuthenticated());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer/profile")
    public ResponseEntity<?> getCustomerProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Customer> customerOpt = customerRepository.findByUsername(auth.getName());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Customer not found");
        }
        return ResponseEntity.ok(customerOpt.get());
    }

    @PostMapping("/customer/subscription")
    public ResponseEntity<?> getCustomerSubscriptions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Customer not found");
        }
        String customerNumber = customerOpt.get().getCustomerNumber();
        List<Subscription> subscriptions = subscriptionRepository.findAllByCustomerNumber(customerNumber);
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/customer/home")
    public ResponseEntity<?> getCustomerHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Customer not found");
        }
        Customer customer = customerOpt.get();
        String customerNumber = customer.getCustomerNumber();

        // Fetch subscriptions
        List<Subscription> subscriptions = subscriptionRepository.findAllByCustomerNumber(customerNumber);

        // Fetch complaints
        List<Complaint> complaints = complaintRepository.findAllByCustomerNumber(customerNumber);

        // Build machines array
        List<Map<String, Object>> machines = new ArrayList<>();
        for (Subscription sub : subscriptions) {
            Map<String, Object> machine = new HashMap<>();
            machine.put("id", sub.getMachineId());
            machine.put("model", "ChapatiMaker Pro"); // Replace with actual model if available
            machine.put("serialNumber", sub.getMachineId());
            machine.put("type", "SUBSCRIBED");
            Map<String, Object> subscription = new HashMap<>();
            subscription.put("id", sub.getId());
            subscription.put("totalRotis", sub.getRotisAllotted());
            subscription.put("rotisMade", sub.getRotisMade());
            // Defensive: calculate rotiLeft if not present
            int rotiLeft = sub.getRotiLeft();
            if (rotiLeft == 0 && sub.getRotisAllotted() > 0) {
                rotiLeft = sub.getRotisAllotted() - sub.getRotisMade();
            }
            subscription.put("rotiLeft", rotiLeft);
            subscription.put("endDate", sub.getEndDate());
            machine.put("subscription", subscription);
            machine.put("alerts", new ArrayList<>()); // Add alerts if available
            machines.add(machine);
        }
        // TODO: Add bought machines if you have them

        // Build complaints array
        List<Map<String, Object>> complaintList = new ArrayList<>();
        for (Complaint c : complaints) {
            Map<String, Object> comp = new HashMap<>();
            comp.put("id", c.getId());
            comp.put("subject", c.getSubject());
            comp.put("status", c.getStatus());
            comp.put("createdAt", c.getCreatedAt());
            comp.put("assignedTo", c.getAssignedTo());
            complaintList.add(comp);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("machines", machines);
        response.put("complaints", complaintList);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/company/machine/{machineId}/notifications")
    public ResponseEntity<?> deleteAllNotifications(@PathVariable String machineId) {
        machineNotificationRepository.deleteByMachineId(machineId);
        return ResponseEntity.ok("All notifications deleted");
    }

    @PostMapping("/customer/subscription/renew")
    public ResponseEntity<?> renewCustomerSubscription(@RequestBody RenewRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("[DEBUG] Renew endpoint called by user: " + auth.getName() + ", authorities: " + auth.getAuthorities());
        String username = auth.getName();
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isEmpty()) {
            System.out.println("[DEBUG] Customer not found for username: " + username);
            return ResponseEntity.status(404).body("Customer not found");
        }
        String customerNumber = customerOpt.get().getCustomerNumber();
        // Find subscription by machineId and customerNumber
        Optional<Subscription> subOpt = subscriptionRepository.findByMachineIdAndCustomerNumber(request.getMachineId(), customerNumber);
        if (subOpt.isEmpty()) {
            System.out.println("[DEBUG] Subscription not found for machineId: " + request.getMachineId());
            return ResponseEntity.status(404).body("Subscription not found");
        }
        Subscription subscription = subOpt.get();
        if (!subscription.getCustomerNumber().equals(customerNumber)) {
            System.out.println("[DEBUG] Unauthorized: subscription.customerNumber=" + subscription.getCustomerNumber() + ", customerNumber=" + customerNumber);
            return ResponseEntity.status(403).body("Unauthorized");
        }
        // subscription.setRenewal("yes");
        subscription.setRenewalApplied(true);
        subscription.setRenewalChapatis(request.getRenewalChapatis());
        subscription.setRenewalStart(request.getRenewalStart());
        subscription.setRenewalEnd(request.getRenewalEnd());
        subscriptionRepository.save(subscription);
        System.out.println("[DEBUG] Subscription renewed successfully for id: " + subscription.getId());
        return ResponseEntity.ok(subscription);
    }

    // ADMIN: Get all subscriptions where renewalApplied is true
    @PostMapping("/admin/renewal-requests")
    public ResponseEntity<?> getAllRenewalRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("[DEBUG] /admin/renewal-requests called by user: " + auth.getName());
        System.out.println("[DEBUG] Authorities: " + auth.getAuthorities());
        List<Subscription> pendingRenewals = subscriptionRepository.findAllByRenewalAppliedTrue();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Subscription sub : pendingRenewals) {
            Map<String, Object> map = new HashMap<>();
            map.put("subscriptionId", sub.getId());
            map.put("machineId", sub.getMachineId());
            map.put("rotisAllotted", sub.getRotisAllotted());
            map.put("renewalChapatis", sub.getRenewalChapatis());
            map.put("renewalStart", sub.getRenewalStart());
            map.put("renewalEnd", sub.getRenewalEnd());
            map.put("renewalApplied", sub.isRenewalApplied());
            // Fetch customer name from repository
            Customer customer = customerRepository.findByCustomerNumber(sub.getCustomerNumber());
            map.put("customerName", customer != null ? customer.getCustomerName() : sub.getCustomerNumber());
            result.add(map);
        }
        System.out.println("[DEBUG] Returning " + result.size() + " renewal requests");
        return ResponseEntity.ok(result);
    }

    // ADMIN: Approve a renewal request by machineId
    @PostMapping("/admin/approve-renewal")
    public ResponseEntity<?> approveRenewal(@RequestBody ApproveRenewalRequest req) {
        // Find subscription by machineId
        Optional<Subscription> subOpt = subscriptionRepository.findByMachineId(req.getMachineId());
        if (subOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Subscription not found for machineId: " + req.getMachineId());
        }
        Subscription sub = subOpt.get();
        if (!sub.isRenewalApplied()) {
            return ResponseEntity.badRequest().body("Renewal not applied for this subscription");
        }
        // Update subscription details
        sub.setRotisAllotted(req.getRotisAllotted());
        sub.setStartDate(req.getStartDate());
        sub.setEndDate(req.getEndDate());
        sub.setRenewalApplied(false);
        // Optionally clear renewal fields
        sub.setRenewalChapatis(0);
        sub.setRenewalStart(null);
        sub.setRenewalEnd(null);
        subscriptionRepository.save(sub);
        return ResponseEntity.ok("Renewal approved and subscription updated");
    }

    // CUSTOMER: Enable or disable a machine subscription
    @PostMapping("/customer/subscription/{machineId}/toggle-enabled")
    public ResponseEntity<?> toggleMachineEnabled(@PathVariable String machineId, @RequestBody Map<String, Boolean> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Customer not found");
        }
        String customerNumber = customerOpt.get().getCustomerNumber();
        Optional<Subscription> subOpt = subscriptionRepository.findByMachineIdAndCustomerNumber(machineId, customerNumber);
        if (subOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Subscription not found");
        }
        Subscription subscription = subOpt.get();
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().body("Missing 'enabled' field in request body");
        }
        subscription.setEnabled(enabled);
        subscriptionRepository.save(subscription);
        Map<String, Object> response = new HashMap<>();
        response.put("machineId", machineId);
        response.put("enabled", enabled);
        return ResponseEntity.ok(response);
    }

    public static class RenewRequest {
        private String machineId;
        private int renewalChapatis;
        private java.time.LocalDate renewalStart;
        private java.time.LocalDate renewalEnd;
        // Getters and setters
        public String getMachineId() { return machineId; }
        public void setMachineId(String machineId) { this.machineId = machineId; }
        public int getRenewalChapatis() { return renewalChapatis; }
        public void setRenewalChapatis(int renewalChapatis) { this.renewalChapatis = renewalChapatis; }
        public java.time.LocalDate getRenewalStart() { return renewalStart; }
        public void setRenewalStart(java.time.LocalDate renewalStart) { this.renewalStart = renewalStart; }
        public java.time.LocalDate getRenewalEnd() { return renewalEnd; }
        public void setRenewalEnd(java.time.LocalDate renewalEnd) { this.renewalEnd = renewalEnd; }
    }

    public static class ApproveRenewalRequest {
        private String machineId;
        private int rotisAllotted;
        private java.time.LocalDate startDate;
        private java.time.LocalDate endDate;
        // Getters and setters
        public String getMachineId() { return machineId; }
        public void setMachineId(String machineId) { this.machineId = machineId; }
        public int getRotisAllotted() { return rotisAllotted; }
        public void setRotisAllotted(int rotisAllotted) { this.rotisAllotted = rotisAllotted; }
        public java.time.LocalDate getStartDate() { return startDate; }
        public void setStartDate(java.time.LocalDate startDate) { this.startDate = startDate; }
        public java.time.LocalDate getEndDate() { return endDate; }
        public void setEndDate(java.time.LocalDate endDate) { this.endDate = endDate; }
    }
}
