package com.rotido.backend.repository;

import com.rotido.backend.model.MachineNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MachineNotificationRepository extends MongoRepository<MachineNotification, String> {
    List<MachineNotification> findByMachineId(String machineId);
    void deleteByMachineId(String machineId);
}
