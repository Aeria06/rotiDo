package com.rotido.backend.repository;

import com.rotido.backend.model.SubscribedMachine;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscribedMachineRepository extends MongoRepository<SubscribedMachine, String> {
}
