package com.rotido.backend.repository;

import com.rotido.backend.model.SoldMachine;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SoldMachineRepository extends MongoRepository<SoldMachine, String> {
    List<SoldMachine> findByClientNumber(String clientNumber);
    List<SoldMachine> findAllByMachineId(String machineId);
}
