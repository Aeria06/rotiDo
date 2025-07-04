package com.rotido.backend.controller.company;
import com.rotido.backend.model.Subscription;
import com.rotido.backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/company")
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // Get all subscriptions
    @PostMapping("/subscriptions")
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

   
    @PostMapping("/subscriptions/day")
public List<Subscription> getSubscriptionsEndingToday() {
    LocalDate today = LocalDate.now();
    LocalDate startOfDay = today;
    LocalDate endOfDay = today.plusDays(1); // exclusive

    return subscriptionRepository.findAllByEndDateBetween(startOfDay, endOfDay);
}


    // Get subscriptions ending in the current month
    @PostMapping("/subscriptions/month")
    public List<Subscription> getSubscriptionsEndingThisMonth() {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonth());
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        return subscriptionRepository.findAllByEndDateBetween(startOfMonth, endOfMonth);
    }

    @PutMapping("/subscriptions/renew")
    public Subscription renewSubscription(@RequestBody RenewRequest request, Principal principal) {
        // Find subscription by ID
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Ensure only the customer who owns the subscription can renew
        String customerNumber = principal.getName(); // Assumes username is customerNumber
        if (!subscription.getCustomerNumber().equals(customerNumber)) {
            throw new RuntimeException("Unauthorized");
        }

        subscription.setRenewal("yes");
        subscription.setRenewalChapatis(request.getRenewalChapatis());
        subscription.setRenewalStart(request.getRenewalStart());
        subscription.setRenewalEnd(request.getRenewalEnd());
        return subscriptionRepository.save(subscription);
    }

    // DTO for renew request
    public static class RenewRequest {
        private String subscriptionId;
        private int renewalChapatis;
        private java.time.LocalDate renewalStart;
        private java.time.LocalDate renewalEnd;
        // Getters and setters
        public String getSubscriptionId() { return subscriptionId; }
        public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
        public int getRenewalChapatis() { return renewalChapatis; }
        public void setRenewalChapatis(int renewalChapatis) { this.renewalChapatis = renewalChapatis; }
        public java.time.LocalDate getRenewalStart() { return renewalStart; }
        public void setRenewalStart(java.time.LocalDate renewalStart) { this.renewalStart = renewalStart; }
        public java.time.LocalDate getRenewalEnd() { return renewalEnd; }
        public void setRenewalEnd(java.time.LocalDate renewalEnd) { this.renewalEnd = renewalEnd; }
    }
}


