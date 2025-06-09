package com.rotido.backend.repository;

import com.rotido.backend.model.SoldMachine;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SoldMachineRepository extends MongoRepository<SoldMachine, String> {
}
