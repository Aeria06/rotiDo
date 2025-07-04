package com.rotido.backend.services.company;

import com.rotido.backend.dto.company.DashboardResponse;
import com.rotido.backend.model.Machine;
import com.rotido.backend.repository.MachineRepository;
import com.rotido.backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public DashboardResponse getDashboardData() {
        long totalMachines = machineRepository.count();

        long machinesSubscribed = machineRepository.countByStatus("subscribed");
        long machinesSold = machineRepository.countByStatus("sold");

        // Sum up totalChapatisMade from all machines
        List<Machine> allMachines = machineRepository.findAll();
        long totalChapatisMade = allMachines.stream()
                .mapToLong(Machine::getTotalChapatisMade)
                .sum();

        LocalDate today = LocalDate.now();
        long subscriptionsEndingToday = subscriptionRepository.countByEndDate(today);

        // Get range for current month
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        long subscriptionsEndingThisMonth = subscriptionRepository.countByEndDateBetween(startOfMonth, endOfMonth);

        return new DashboardResponse(
                (int) totalMachines,
                (int) machinesSubscribed,
                (int) machinesSold,
                (int) totalChapatisMade,
                (int) subscriptionsEndingToday,
                (int) subscriptionsEndingThisMonth
        );
    }
}
    