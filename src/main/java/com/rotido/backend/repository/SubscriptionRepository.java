package com.rotido.backend.repository;

import com.rotido.backend.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findAllByCustomerNumber(String customerNumber);

    List<Subscription> findAllByEndDate(LocalDate endDate);

    List<Subscription> findByCustomerNumber(String customerNumber);
    List<Subscription> findAllByMachineId(String machineId);

    List<Subscription> findAllByEndDateBetween(LocalDate start, LocalDate end);

    long countByEndDate(LocalDate endDate);

    long countByEndDateBetween(LocalDate start, LocalDate end);

    Optional<Subscription> findByMachineIdAndCustomerNumber(String machineId, String customerNumber);

    List<Subscription> findAllByRenewalAppliedTrue();

    Optional<Subscription> findByMachineId(String machineId);
}
